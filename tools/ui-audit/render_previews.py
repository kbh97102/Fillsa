# -*- coding: utf-8 -*-
"""Render saved cases through Android CLI, retaining evidence and resumable results."""
import argparse
import hashlib
import json
import struct
import subprocess
import time
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
ART = ROOT / 'docs/design-qa/artifacts/2026-10-25-full-screen-audit'
parser=argparse.ArgumentParser()
parser.add_argument('--limit',type=int,default=1000)
parser.add_argument('--id',action='append',default=[])
parser.add_argument('--retry',action='store_true')
args=parser.parse_args()
manifest=json.loads((ART/'preview-manifest.json').read_text())
results_path=ART/'render-results.json'
results=json.loads(results_path.read_text()) if results_path.exists() else {}
cases=json.loads((ART/'screen-map.json').read_text())
by_id={c['id']:c for c in cases}
source_files=sorted(p for mod in ['app','presentation','domain','data','compose-util'] for p in (ROOT/mod/'src').rglob('*.kt'))
source_hash=hashlib.sha256(b''.join(str(p.relative_to(ROOT)).encode()+p.read_bytes() for p in source_files)).hexdigest()
# Lower component previews before assembled screens. No UI acceptance is inferred from rendering.
manifest.sort(key=lambda m: (by_id[m['id']]['android'].get('widthDp',360)>=360,m['file'],m['id']))
completed=0
for item in manifest:
    key=item['id']
    if args.id and key not in args.id:continue
    if key in results and not args.retry:continue
    if completed>=args.limit:break
    image=ART/'android'/f'{key}.png';log=ART/'semantics'/f'{key}.txt'
    command=[str(Path.home()/'.local/bin/android'),'studio','render-compose-preview',item['file'],item['function'],'--project','Fillsa',f'--output-image-file={image}','--print-semantics']
    start=time.time()
    try:
        proc=subprocess.run(command,cwd=ROOT,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True,timeout=100)
        output=proc.stdout;code=proc.returncode
    except subprocess.TimeoutExpired as exc:
        output=(exc.stdout or b'').decode(errors='replace') if isinstance(exc.stdout,bytes) else (exc.stdout or '')
        output+='\nRender timed out after 100 seconds.';code=124
    log.write_text(output)
    ok=code==0 and image.exists() and image.read_bytes()[:8]==b'\x89PNG\r\n\x1a\n'
    result=dict(command=command,exitCode=code,status='rendered' if ok else 'failed',durationSeconds=round(time.time()-start,2),sourceSha256=source_hash,log=str(log.relative_to(ROOT)))
    c=by_id[key];c['android']['renderLog']=str(log.relative_to(ROOT))
    if ok:
        raw=image.read_bytes();w,h=struct.unpack('>II',raw[16:24])
        result.update(image=str(image.relative_to(ROOT)),width=w,height=h,sha256=hashlib.sha256(raw).hexdigest())
        c['android'].update(image=result['image'],semantics=result['log'],renderWidth=w,renderHeight=h,sourceSha256=source_hash)
        c['captureStatus']='preview_ready'
    else:
        result['error']='\n'.join(output.splitlines()[:3])
        c['captureStatus']='blocked'
        c['notes']=[n for n in c['notes'] if not n.startswith('Preview 렌더 실패:')]+['Preview 렌더 실패: '+result['error']]
    results[key]=result
    results_path.write_text(json.dumps(results,ensure_ascii=False,indent=2))
    (ART/'screen-map.json').write_text(json.dumps(cases,ensure_ascii=False,indent=2))
    print(f'{key}: {result["status"]} ({result["durationSeconds"]}s)',flush=True)
    completed+=1
print(f'{completed} attempted; {sum(r["status"]=="rendered" for r in results.values())}/{len(results)} rendered',flush=True)
