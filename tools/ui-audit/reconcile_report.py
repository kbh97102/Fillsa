# -*- coding: utf-8 -*-
"""Merge extraction/render/review evidence after a capture batch; never invent a pass."""
import hashlib
import json
import re
from pathlib import Path

ROOT=Path(__file__).resolve().parents[2]
ART=ROOT/'docs/design-qa/artifacts/2026-10-25-full-screen-audit'
def read(name, default):
    path=ART/name
    return json.loads(path.read_text()) if path.exists() else default
cases=read('screen-map.json',[])
exports={n['nodeId']:n for n in read('figma-exports.json',[])}
renders=read('render-results.json',{})
manifest={m['id']:m for m in read('preview-manifest.json',[])}
findings=read('review-findings.json',{})
sources={}
for mod in ['app','presentation']:
    for p in (ROOT/mod/'src/main').rglob('*.kt'):
        for name in re.findall(r'\bfun\s+(\w+)\s*\(',p.read_text()):sources[name]=str(p.relative_to(ROOT))
for c in cases:
    exp=exports.get(c['figma'].get('nodeId'))
    if exp and exp.get('downloadExit')==0:
        path=ROOT/exp['path'];c['figma'].update(image=exp['path'],sha256=hashlib.sha256(path.read_bytes()).hexdigest())
        c['notes']=[n for n in c['notes'] if 'MCP 사용량 한도' not in n]
        if c['captureStatus']=='blocked' and not c['android'].get('renderLog') and c['mappingStatus']!='figma_only':c['captureStatus']='figma_ready'
        if c['comparisonStatus']=='not_comparable' and c['mappingStatus']=='candidate':c['comparisonStatus']='not_reviewed'
    a=c['android']
    if c['id'] in manifest:a.update(previewFile=manifest[c['id']]['file'],previewFunction=manifest[c['id']]['function'])
    c['notes']=[n for n in c['notes'] if n != '기능·본문·표시 상태로 후보 연결. 이미지 검토 후 확정.']
    source=sources.get(a.get('productionComposable'))
    if source:a['sourceFile']=source
    result=renders.get(c['id'])
    if result and result['status']=='rendered':
        a.update(image=result['image'],semantics=result['log'],renderWidth=result['width'],renderHeight=result['height'],sourceSha256=result['sourceSha256'])
        c['captureStatus']='preview_ready'
        a['pixelsPerDp']=round(result['width']/a.get('widthDp',360),4)
        c['notes']=[n for n in c['notes'] if not n.startswith('Preview 렌더 실패:')]
    review=findings.get(c['id'],{})
    for k,v in review.items():
        if k in ['android','figma','fixture']:c[k].update(v)
        elif k=='replaceNotes':c['notes']=v
        elif k=='notes':c[k]=list(dict.fromkeys(c[k]+v))
        else:c[k]=v
    c['notes']=[n for n in c['notes'] if n != '기능·본문·표시 상태로 후보 연결. 이미지 검토 후 확정.']
    source=sources.get(a.get('productionComposable'))
    if source:a['sourceFile']=source
    if not a.get('image') and not a.get('runtimeImage') and c['mappingStatus'] in ['component_reference','platform_only','figma_only']:
        c['comparisonStatus']='not_comparable'

ids=[c['id'] for c in cases];assert len(ids)==len(set(ids))
routes=set(read('android-inventory.json',{})['routes'])
assert routes <= {c['android'].get('route') for c in cases},routes-{c['android'].get('route') for c in cases}
for c in cases:
    for side in ['figma','android']:
        path=c[side].get('image')
        if path:assert (ROOT/path).read_bytes()[:8]==b'\x89PNG\r\n\x1a\n',path
(ART/'screen-map.json').write_text(json.dumps(cases,ensure_ascii=False,indent=2)+'\n')
print(f'{len(cases)} unique rows; all {len(routes)} routes accounted for; PNG signatures verified.')
