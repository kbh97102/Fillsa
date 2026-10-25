# -*- coding: utf-8 -*-
"""Build an offline, evidence-linked HTML audit from the saved ledger."""
import base64
import html
import json
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
ART = ROOT / 'docs/design-qa/artifacts/2026-10-25-full-screen-audit'
DEST = ROOT / 'docs/design-qa/2026-10-25-full-screen-audit.html'
rows = json.loads((ART / 'screen-map.json').read_text())
baseline = json.loads((ART / 'source-baseline.json').read_text())
def esc(v): return html.escape(str(v), quote=True)
def pic(path, label, width=360):
    if not path or not (ROOT/path).is_file(): return '<div class="missing">'+esc(label)+' 이미지 미확보</div>'
    raw=(ROOT/path).read_bytes(); pixel_width,pixel_height=struct.unpack('>II',raw[16:24])
    data = base64.b64encode(raw).decode()
    return f'<a href="../../{esc(path)}" target="_blank"><img loading="lazy" width="{pixel_width}" height="{pixel_height}" style="width:{int(width)}px;height:auto;max-height:none" src="data:image/png;base64,{data}" alt="{esc(label)}"></a>'
labels={'candidate':'대응 후보','matched':'대응 확인','figma_only':'Android 대응 없음','android_only':'Figma 대응 없음','platform_only':'플랫폼 별도','component_reference':'하위 참조','pending':'대기','figma_ready':'Figma 확보','preview_ready':'Preview 확보','runtime_ready':'설치 앱 캡처','runtime_required':'런타임 추가 필요','blocked':'보류','not_reviewed':'미검토','not_comparable':'직접 비교 보류','difference_found':'차이/조건 차이 관찰','no_observed_difference':'큰 배치 차이 미관찰'}
cards=[]
for row in rows:
    f,a=row['figma'],row['android']; identifier=row['id']
    if row['mappingStatus']=='excluded': continue
    differences=''.join('<li>'+esc(d)+'</li>' for d in row['differences']) or ('<li>큰 배치 차이를 발견하지 못했습니다. 최종 UI 승인은 아닙니다.</li>' if row['comparisonStatus']=='no_observed_difference' else '<li>직접 일치 판정은 보류했습니다. 아래 조건·사유를 확인하세요.</li>')
    notes=''.join('<li>'+esc(n)+'</li>' for n in row['notes'])
    fcaption=f'<a target="_blank" href="{esc(f.get("url","#"))}">{esc(f.get("nodeId","대응 Figma 없음"))}</a> · {f.get("width","—")}×{f.get("height","—")}'
    acaption=esc(a.get('previewFunction') or a.get('productionComposable') or '런타임 전용')
    evidence=''
    for key in ['sourceFile','previewFile','semantics','renderLog']:
        if a.get(key): evidence+=f'<a target="_blank" href="../../{esc(a[key])}">{esc(key)}</a> '
    runtime=''
    if a.get('image') and a.get('runtimeImage'):
        runtime='<details><summary>설치 앱의 런타임 보조 캡처</summary>'+pic(a['runtimeImage'],'설치 앱 · Preview와 다른 데이터')+'<p>1.0.31(25), emulator-5554, API 35, 360×720, density 160. 이번 변경 소스 빌드의 최종 조립 승인은 아닙니다.</p></details>'
    cards.append(f'''<article id="{identifier}" data-feature="{esc(row['feature'])}" data-theme="{row['theme']}" data-status="{row['comparisonStatus']}" data-search="{esc(row['feature']+' '+row['screen']+' '+row['state'])}">
    <header><span>{esc(row['feature'])} · {row['theme']}</span><h2>{esc(row['screen'])}</h2><code>{identifier}</code><p>{esc(labels.get(row['mappingStatus'],row['mappingStatus']))} / {esc(labels.get(row['captureStatus'],row['captureStatus']))} / {esc(labels.get(row['comparisonStatus'],row['comparisonStatus']))}</p></header>
    <div class="pair"><figure><figcaption>Figma · {fcaption}</figcaption>{pic(f.get('image'),'Figma 기준',f.get('width',360))}</figure><figure><figcaption>Android · {acaption}</figcaption>{pic(a.get('image') or a.get('runtimeImage'),'Android',a.get('widthDp',360))}<small>{esc(a.get('captureNote',''))}</small></figure></div>
    <div class="details">{runtime}<h3>확인된 차이 / 비교 상태</h3><ul>{differences}</ul><h3>조건·보류 사유</h3><ul>{notes}</ul><p>{evidence}</p><details><summary>원본 문구·fixture</summary><p>{esc(row['state'])}</p><pre>{esc(json.dumps(row['fixture'],ensure_ascii=False,indent=2))}</pre></details>
    <label>내 결정 <select class="decision" aria-label="{esc(row['screen'])} 결정"><option value="pending">미정</option><option value="fix">수정</option><option value="keep">유지</option><option value="later">나중에</option></select></label><textarea class="note" placeholder="수정 범위·우선순위 메모" aria-label="{esc(row['screen'])} 메모"></textarea></div></article>''')

features=sorted(set(r['feature'] for r in rows if r['mappingStatus']!='excluded'))
stats=dict(figma=sum(bool(r['figma'].get('image')) for r in rows),android=sum(bool(r['android'].get('image')) for r in rows),reviewed=sum(r['comparisonStatus'] in ['difference_found','no_observed_difference'] for r in rows),total=sum(r['mappingStatus'] not in ['excluded','component_reference'] for r in rows))
page='''<!doctype html><html lang="ko"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Fillsa 전체 화면 대조</title><style>
*{box-sizing:border-box}table{width:100%;border-collapse:collapse}th,td{padding:10px;text-align:left;border-bottom:1px solid #dbe1ea}td:first-child{min-width:105px}body{margin:0;font-family:system-ui,-apple-system,sans-serif;color:#253142;background:#eef1f5;line-height:1.65}main{max-width:1280px;margin:auto;padding:28px}h1{font-size:32px;line-height:1.25}h2{margin:6px 0;font-size:23px}h3{font-size:15px}a{color:#315a9c}code{font-size:12px}.intro,.toolbar,article{background:white;border:1px solid #dbe1ea;border-radius:14px;margin-bottom:22px}.intro{padding:28px}.badge{background:#fff1c9;padding:10px 16px;border-radius:8px}.stats{display:flex;gap:14px;flex-wrap:wrap;margin:22px 0}.stats div{padding:12px 20px;background:#f1f5fa;border-radius:10px}.stats b{display:block;font-size:24px}.toolbar{padding:14px;position:sticky;top:0;z-index:5;box-shadow:0 3px 12px #2332}select,input,button,textarea{font:inherit;padding:8px;border:1px solid #bac5d5;border-radius:7px;background:white}input{max-width:250px}button{cursor:pointer;background:#243b63;color:white}article{overflow:hidden;scroll-margin-top:100px}article header,.details{padding:20px 26px}article header{background:#f8fafc}article header span{font-size:13px;color:#60718a}.pair{display:grid;grid-template-columns:1fr 1fr;gap:16px;padding:18px;align-items:start}figure{margin:0;text-align:center;min-width:0}figcaption{font-size:13px;margin-bottom:12px;min-height:22px;overflow-wrap:anywhere}figure img{max-width:100%;width:auto;max-height:850px;object-fit:contain;box-shadow:0 1px 8px #2233}figure small{display:block}.missing{height:220px;display:grid;place-items:center;background:repeating-linear-gradient(45deg,#f4f6f9,#f4f6f9 12px,#eaf0f5 12px,#eaf0f5 24px);color:#61748f}.details{border-top:1px solid #eee}pre{white-space:pre-wrap;font-size:12px}textarea{display:block;width:100%;min-height:75px;margin-top:12px}summary{cursor:pointer}article[hidden]{display:none}.footer{padding:24px}.empty{padding:20px} @media(max-width:700px){main{padding:10px}.intro{padding:20px}h1{font-size:26px}.toolbar{position:static}.pair{padding:8px;gap:8px}.pair img{max-height:650px}.details,article header{padding:15px}article{scroll-margin-top:10px}figcaption{font-size:11px;min-height:40px}.toolbar>*{margin-bottom:8px;max-width:100%}}
</style><main><section class="intro"><p>FILLSA / DESIGN AUDIT / 2026.10.25</p><h1>Figma × 현재 Android<br>전체 화면 비교 보고서</h1><p>기준: <a href="https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-3720">리뉴얼 ver.1.0 · 2929:3720</a></p><p class="badge">검토용 1차 대조 자료 · 수정할 화면은 사용자가 결정합니다. Preview 렌더 성공은 실제 기기 최종 UI 승인이 아닙니다.</p>
<p>화면·팝업·테마별 후보를 실제 문구로 구분했습니다. 디자인의 iOS 시스템 바, 키보드, 스크롤 길이는 Android 콘텐츠와 별도 조건입니다. 이미지 미확보와 UI 미구현은 서로 다른 상태입니다.</p>
<div class="stats">STATS</div>
<h2>먼저 결정할 화면</h2><table><thead><tr><th>기능</th><th>관찰 / 다음 판단</th></tr></thead><tbody>
<tr><td><a href="#figma-2929-11031">My Page</a></td><td>로그아웃·버전 순서와 회원탈퇴 위치. Figma의 3탭/4탭 변형 중 최종 기준 선택.</td></tr>
<tr><td><a href="#figma-2929-11588">공지사항</a></td><td>다크 배경, 빈 상태 위치, 목록/상세 간격을 비교.</td></tr>
<tr><td><a href="#figma-2929-11314">알림·테마</a></td><td>4개 title 토글 예시는 실제 1개 알림 설정과 다른 구성. 적용 범위 결정 필요.</td></tr>
<tr><td><a href="#figma-2929-10941">공유</a></td><td>별밤·수평선 배경 추가 여부, 카드 위치·그라데이션·줄바꿈 검토.</td></tr>
<tr><td><a href="#figma-2929-13556">Home·Calendar</a></td><td>SVG 빈칸과 fixture 차이를 앱 결함으로 확정하지 말고 실제 동일 상태 캡처로 보완.</td></tr></tbody></table>
<h2>비교 조건과 남은 확인</h2><ul><li>모든 14개 앱 route를 목록에 연결했습니다. 위젯·기록 목록/상세/메모는 지정 Figma 페이지의 대응 디자인을 찾지 못해 Android 이미지만 제공합니다.</li><li>Figma 원본은 전체 프레임, Preview는 OS 바 48dp와 광고를 제외한 콘텐츠입니다. 360dp 기준 동일 가로 크기로 표시합니다. 세로 오프셋 자체를 결함으로 단정하지 않습니다.</li><li>Home/Calendar의 일부 SVG와 Popup, 토스트·IME는 Preview 제약이 있습니다. 시스템 사진 선택기는 비회원 로그인 안내로 진입이 막혔습니다.</li><li>연도·월 선택창, 키보드, 공유창과 Home은 설치 버전 1.0.31(25)의 보조 캡처입니다. 실제 Glance 런처 위젯·권한창 및 이번 소스 빌드의 전체 화면 승인은 남아 있습니다.</li><li>팝업 Preview는 기존 production 본문을 재사용합니다. 일반 팝업 dim은 0.6 근사값, 필사 결과는 코드에 지정된 값입니다. 창 그림자는 런타임 확인이 필요합니다.</li><li>이번 비교는 시각/상태 조사입니다. 전 기능 동작·서버 통신·배포 QA 완료를 뜻하지 않습니다.</li></ul>
<p><a href="artifacts/2026-10-25-full-screen-audit/verification.json">컴파일·단위 테스트 결과</a> · <a href="artifacts/2026-10-25-full-screen-audit/render-results.json">CLI 명령·렌더 로그 목록</a></p>
<p>기준 commit <code>COMMIT</code> 및 미커밋 소스 포함. <a href="artifacts/2026-10-25-full-screen-audit/source-baseline.json">소스 해시</a> · <a href="../superpowers/plans/2026-10-25-full-screen-figma-audit.md">실행 계획</a> · <a href="artifacts/2026-10-25-full-screen-audit/screen-map.json">전체 비교 목록</a></p>
<details><summary>비교 순서와 판정 기준</summary><ol><li>기능별 프레임·문구·상태 연결</li><li>Figma 원본 PNG와 고정 데이터 확보</li><li>실제 앱 컴포넌트를 호출하는 결정적 Preview 마련</li><li>Android CLI 이미지·semantics 저장 및 시각 비교</li><li>시스템 UI는 런타임 캡처로 별도 검증</li><li>사용자가 수정·유지·나중에 선택</li></ol><p>스토어 홍보 이미지 21개는 비교 대상에서 제외하고 전체 목록에 보존했습니다. 다른 페이지의 오래된 디자인을 임의 기준으로 사용하지 않습니다.</p></details></section>
<nav class="toolbar"><input id="search" type="search" placeholder="화면·문구 검색" aria-label="검색"><select id="feature"><option value="">전체 기능</option>FEATURES</select><select id="theme"><option value="">모든 테마</option><option>Light</option><option>Dark</option><option>System</option></select><select id="status"><option value="">모든 비교 상태</option><option value="difference_found">차이 확인</option><option value="not_reviewed">미검토</option><option value="not_comparable">비교 불가</option><option value="no_observed_difference">관찰된 차이 없음</option></select><button id="export">결정 JSON 내보내기</button><span id="count"></span><small id="storage"></small></nav>
CARDS<footer class="footer">결정은 이 브라우저에 저장됩니다. JSON으로 내보내면 후속 수정 계획에 사용할 수 있습니다. 앱·Figma·Notion에는 자동 반영하지 않습니다.</footer></main><script>
const key='fillsa-full-audit-20261025';let decisions={};try{decisions=JSON.parse(localStorage.getItem(key)||'{}')}catch(e){document.querySelector('#storage').textContent='브라우저 저장 불가: JSON 내보내기를 사용하세요.'}
const cards=[...document.querySelectorAll('article')];for(const c of cards){const d=decisions[c.id]||{};c.querySelector('.decision').value=d.decision||'pending';c.querySelector('.note').value=d.note||'';const save=()=>{decisions[c.id]={decision:c.querySelector('.decision').value,note:c.querySelector('.note').value};try{localStorage.setItem(key,JSON.stringify(decisions))}catch(e){}};c.querySelector('.decision').onchange=save;c.querySelector('.note').oninput=save}
function filter(){let shown=0;for(const c of cards){const match=(!feature.value||c.dataset.feature===feature.value)&&(!theme.value||c.dataset.theme===theme.value)&&(!statusFilter.value||c.dataset.status===statusFilter.value)&&c.dataset.search.toLowerCase().includes(search.value.toLowerCase());c.hidden=!match;if(match)shown++}document.querySelector('#count').textContent=` ${shown}개 항목`}
const search=document.querySelector('#search'),feature=document.querySelector('#feature'),theme=document.querySelector('#theme'),statusFilter=document.querySelector('#status');[search,feature,theme,statusFilter].forEach(e=>e.addEventListener('input',filter));filter();document.querySelector('#export').onclick=()=>{const blob=new Blob([JSON.stringify({report:'2026-10-25-full-screen-audit',exportedAt:new Date().toISOString(),decisions},null,2)],{type:'application/json'});const a=document.createElement('a');a.href=URL.createObjectURL(blob);a.download='fillsa-ui-decisions.json';a.click();setTimeout(()=>URL.revokeObjectURL(a.href),1000)};
</script></html>'''
stats_html=''.join(f'<div><b>{stats[k]}</b>{label}</div>' for k,label in [('total','화면·상태 후보'),('figma','Figma 이미지 확보'),('android','Android Preview 확보'),('reviewed','차이/배치 관찰 기록')])
page=page.replace('STATS',stats_html).replace('COMMIT',baseline['head'][:12]).replace('FEATURES',''.join(f'<option>{esc(f)}</option>' for f in features)).replace('CARDS','\n'.join(cards))
DEST.write_text(page)
print(f'{DEST}: {len(cards)} cards, {DEST.stat().st_size:,} bytes; {stats}')
