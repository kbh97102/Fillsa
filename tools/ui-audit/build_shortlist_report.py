# -*- coding: utf-8 -*-
"""Build a separate curated report. The original audit and its evidence are read only."""
import base64
import hashlib
import html
import json
import struct
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2]
SOURCE=ROOT/'docs/design-qa/2026-10-25-full-screen-audit.html'
ART=ROOT/'docs/design-qa/artifacts/2026-10-25-full-screen-audit'
OUT=ROOT/'docs/design-qa/artifacts/2026-10-25-ui-fix-shortlist'
DEST=ROOT/'docs/design-qa/2026-10-25-ui-fix-shortlist.html'
EXPECTED_SOURCE='a7f6075ccc6cd19e17e1d9864cefbd95065e6f416f691b731d1313a9902af171'
assert hashlib.sha256(SOURCE.read_bytes()).hexdigest()==EXPECTED_SOURCE,'Original audit changed; review the source before regenerating.'
rows={c['id']:c for c in json.loads((ART/'screen-map.json').read_text())}
def ids(*values):return ['figma-'+v for v in values]
def group(key,title,kind,priority,frames,why,scope,check,limit,code,related=()):
 return dict(id=key,title=title,kind=kind,priority=priority,representatives=ids(*frames),related=ids(*(related or frames)),why=why,scope=scope,acceptance=check,limit=limit,code=code)
groups=[
 group('common-dialog','공통 팝업 · 다크 아이콘과 오류 안내 글꼴','수정 후보','먼저 검토',
 ['2929-6732','2929-6856','2929-5975','2929-6283'],
 ['다크 업데이트·알림 팝업의 Figma 아이콘은 노란색이고 Android는 보라·파란색이다.','네트워크 오류 안내는 실제 DialogData의 Heading4 설정으로 렌더링했을 때 Figma보다 굵고 크게 보인다.'],
 ['테마별 경고·알림 아이콘 자산을 Figma에 맞춘다.','네트워크 오류 팝업의 제목 스타일·줄간격·내부 여백을 조정한다.'],
 '팝업 본문 단독 Preview로 아이콘·문구·버튼을 비교한 뒤 실제 Dialog 창에서 잘림과 버튼 접근성을 확인한다.',
 '팝업 뒤 Home의 이전 4탭 구성과 Preview dim 0.6은 수정 근거에 포함하지 않는다. 모든 팝업을 한 번에 같은 크기로 바꾸는 제안이 아니다.',
 ['presentation/src/main/java/com/arakene/presentation/ui/common/UpdateDialog.kt','presentation/src/main/java/com/arakene/presentation/ui/common/DialogWithImage.kt','presentation/src/main/java/com/arakene/presentation/util/DialogData.kt'],
 ['2929-6732','2929-6626','2929-6856','2929-6747','2929-5975','2929-6283']),
 group('login','로그인 · 설명 문구 정렬','수정 후보','먼저 검토',
 ['2929-3723','2929-5815'],
 ['Figma 설명 문구는 가운데 정렬인데 Android에서는 왼쪽 정렬과 다른 줄바꿈이 보인다.','LoginContent의 설명 Text에는 명시적인 가운데 정렬이 없다.'],
 ['설명 문구의 가로 영역·정렬·줄바꿈을 Figma에 맞춘다.','로고와 버튼 사이 간격은 OS 바가 같은 런타임에서 확인한 뒤 필요한 부분만 조정한다.'],
 'Light/Dark에서 설명 두 줄의 가운데 정렬을 확인하고, 360×720 실제 화면에서 약관까지 잘리지 않는지 확인한다.',
 'Apple 버튼이 포함된 다른 로그인 프레임은 제외했다. 하단 약관 잘림은 추가 확인 항목이며 이번 보고서에서 확정하지 않는다.',
 ['presentation/src/main/java/com/arakene/presentation/ui/LoginView.kt']),
 group('alert','알림 설정 · 스위치 크기와 안내 행','수정 후보','다음 검토',
 ['2929-11260','2929-11435'],
 ['같은 알림 켜짐 상태에서 Android 스위치가 Figma보다 크게 보인다.','안내 문구를 포함한 행의 높이와 내부 간격도 다르다.'],
 ['Figma 스위치의 트랙·손잡이 크기와 설명 행 간격을 확인해 조정한다.','터치 영역과 접근성은 유지하면서 보이는 크기를 맞춘다.'],
 'Light/Dark 및 켜짐/꺼짐 상태를 대조하고, 실제 화면에서 토글 동작·터치 영역을 확인한다.',
 '회원탈퇴 메뉴 위치 변경은 아래 계정 메뉴 항목에서 별도 결정한다. 알림 전송이나 OS 권한 동작의 결함 판정이 아니다.',
 ['presentation/src/main/java/com/arakene/presentation/ui/mypage/AlertSwitchSection.kt']),
 group('calendar-dark','Calendar · 다크 달력 배경','수정 후보','다음 검토',
 ['3051-899','3051-1130'],
 ['답변 미작성/작성 완료 두 Figma에서 달력 패널이 Android보다 어둡다.','현재 CalendarSection은 다크 달력 패널을 #424242로 지정하고 있다. 색상 차이는 날짜 fixture와 별개다.'],
 ['두 대상 Figma에서 패널 채움색·테두리를 확인해 CalendarSection에 반영한다.'],
 '같은 날짜·선택 상태로 두 다크 Preview를 대조하고, 달력 숫자 대비와 선택일 가독성을 실제 화면에서 확인한다.',
 '화살표·로고·완료 뱃지의 빈칸은 SVG Preview 제약 때문에 이번 수정 후보에서 제외했다. 날짜 셀 차이도 포함하지 않는다.',
 ['presentation/src/main/java/com/arakene/presentation/ui/calendar/CalendarSection.kt']),
 group('share-gradient','공유 카드 · 그라데이션 방향','수정 후보','다음 검토',
 ['2929-18581','2929-18577','2929-18585'],
 ['같은 270×400 카드에서 Figma와 Android의 그라데이션 방향·색상 분포가 다르다.','예를 들어 02 배경의 앱 벡터는 X=135에서 위→아래로 진행하는 선형 그라데이션이다.'],
 ['Figma 원본의 그라데이션 좌표·색상 정지점을 추출해 해당 배경 자산을 갱신한다.','동일 자산을 쓰는 다크 공유 카드도 함께 확인한다.'],
 '동일 270×400 카드와 문구로 배경 자체를 먼저 비교한다. 본문 글꼴·줄바꿈은 별도 측정 후 변경 여부를 정한다.',
 '캐러셀 좌우 카드 노출은 선택 인덱스 차이일 수 있어 제외했다. 공유 저장·전송 동작은 수정 대상에 포함하지 않는다.',
 ['presentation/src/main/res/drawable/img_share_background_1.xml','presentation/src/main/res/drawable/img_share_background_2.xml','presentation/src/main/res/drawable/img_share_background_3.xml'],
 ['2929-18581','2929-10927','2929-18577','2929-10923','2929-18585','2929-10931']),
 group('account-menu','My Page · 로그아웃·버전·회원탈퇴 위치','기준 결정 후','결정 필요',
 ['2929-11031','2929-11613'],
 ['Figma는 로그아웃 → 버전 → 회원탈퇴 순서지만 앱은 버전 → 로그아웃 순서다.','앱의 회원탈퇴는 알림 설정 화면에 있어 기능 위치도 다르다.'],
 ['대상 Figma의 계정 메뉴 구성을 적용할지 먼저 정한다.','채택하면 메뉴 순서를 맞추고 탈퇴 진입점을 옮기되, 기존 확인·취소 흐름은 보존한다.'],
 '회원/비회원 My Page 및 알림 설정에서 메뉴 노출과 이동을 확인하고, 기존 위치에 중복 진입점이 남지 않는지 검증한다.',
 '탈퇴는 실제 계정 동작으로 이어지므로 시각 차이만 보고 이동을 확정하지 않았다. 버전 1.0과 1.0.31의 값 차이는 제외했다.',
 ['presentation/src/main/java/com/arakene/presentation/ui/mypage/MyPageBottomButtonSection.kt','presentation/src/main/java/com/arakene/presentation/ui/mypage/AlertView.kt']),
 group('typing','필사 입력 · 상단 캐릭터·안내 및 다크 배경','기준 결정 후','결정 필요',
 ['3087-28815','2929-17920'],
 ['Figma 입력 화면의 상단 캐릭터와 안내 문구가 현재 TypingQuoteContent에는 없다. SVG 로딩 실패와 다른 코드 구성 차이다.','Dark 입력 배경도 현재 gray_600으로 더 밝게 보인다.'],
 ['Light의 write-as is와 Dark의 write-to be 중 실제 채택할 구성을 먼저 확정한다.','채택한 기준에 따라 캐릭터·안내 영역과 배경색을 조정하고 입력 영역의 높이를 다시 배치한다.'],
 '입력 전/중/저장 직후를 같은 문구로 검증한다. 키보드가 열린 실제 화면에서 저장 버튼과 본문이 가려지지 않는지 확인한다.',
 '기존 보고서에는 as-is/to-be가 함께 있다. 이전 작업에서 의도한 제거인지 확인하기 전에는 캐릭터 복원을 확정하지 않는다. 결과 팝업 자체는 이번 후보에서 제외했다.',
 ['presentation/src/main/java/com/arakene/presentation/ui/home/TypingQuoteView.kt'],
 ['3087-28815','2929-17920','2929-9764','2929-18278']),
 group('share-missing','공유 배경 · 별밤·바다 수평선 추가 여부','기준 결정 후','결정 필요',
 ['2929-18593','2929-18597'],
 ['Figma의 별밤·바다 수평선 배경에 대응하는 선택 항목이 현재 ShareContent의 8개 자산 목록에 없다.'],
 ['두 배경을 실제 공유 선택 목록에 포함할지 결정한다.','포함한다면 원본 자산을 확보해 공유 카드 및 저장 결과에 반영한다.'],
 '선택 목록에서 각 배경을 표시하고, 저장된 이미지의 배경·텍스트 가독성·크기를 검증한다.',
 'Android 대응 이미지가 없어 미구현 후보로 제시한다. 비슷한 다른 배경을 비교 이미지로 대체하지 않는다.',
 ['presentation/src/main/java/com/arakene/presentation/ui/home/ShareView.kt'],
 ['2929-18593','2929-10941','2929-18597','2929-10945']),
]
exclusions=[
 ('공지사항 다크 배경·구분선','원본 픽셀 재확인에서 빈 상태의 배경은 양쪽 모두 #212121, 목록·상세 배경은 #424242였다. 구분선 차이도 확실하지 않아 수정 후보에서 제외했다.'),
 ('Home 전반·이미지 등록 전/후','SVG 빈칸, 답변 fixture, 광고 제외와 화면 높이가 섞여 있어 현재 증거만으로 특정 UI 수정을 정하기 어렵다.'),
 ('온보딩·스플래시·세로 위치만 다른 화면','OS 바 및 애니메이션 시점·캡처 크기 차이를 먼저 맞춰야 한다.'),
 ('테마·이미지/삭제/탈퇴 팝업의 막연한 높이 차이','이전 보고서의 넓은 차이 기록만으로 내부 요소의 실제 수정량을 확정하기 어렵다. 별도 창의 런타임 기하 정보도 필요하다.'),
 ('필사 결과 팝업 하위 컴포넌트 6개','이전 시각 검토에서 큰 배치 차이를 발견하지 못했다.'),
 ('위젯·기록 목록/상세/메모','지정 Figma 페이지의 대응 기준이 없다.'),
 ('Apple 로그인·IME·권한창·공유창·런처','플랫폼 차이 또는 추가 런타임 증거가 필요하다.'),
]
all_ids=[x for g in groups for x in g['representatives']]
assert len(groups)==8 and len(all_ids)==19 and len(set(all_ids))==19
assert all(i in rows for g in groups for i in g['related'])
OUT.mkdir(parents=True,exist_ok=True)
manifest=dict(source=str(SOURCE.relative_to(ROOT)),sourceSha256=EXPECTED_SOURCE,selectionRule='동일 상태의 색상·자산·정렬·구성 차이가 구체적이거나 코드와 함께 확인되는 후보. 데이터/플랫폼/Preview 조건 차이만으로 선정하지 않음.',groups=groups,excluded=exclusions,representativeCount=len(all_ids))
(OUT/'selection.json').write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n')
def esc(s):return html.escape(str(s),quote=True)
def bullet(xs):return '<ul>'+''.join('<li>'+esc(x)+'</li>' for x in xs)+'</ul>'
def img(path,width=360):
 if not path:return '<div class="missing"><b>대응하는 앱 배경 없음</b><p>현재 8개 배경 선택 목록에 해당 자산이 없습니다.</p></div>'
 data=(ROOT/path).read_bytes();assert data[:8]==b'\x89PNG\r\n\x1a\n';w,h=struct.unpack('>II',data[16:24]);b64=base64.b64encode(data).decode()
 return f'<img loading="lazy" width="{w}" height="{h}" style="width:{width}px;height:auto" src="data:image/png;base64,{b64}" alt="비교 기준 이미지">'
def pair(id):
 c=rows[id];f,a=c['figma'],c['android'];width=f.get('width',360)
 return f'''<section class="evidence"><h4>{esc(c['screen'])} · {esc(c['theme'])} <small>{esc(f['nodeId'])}</small></h4><div class="pair"><figure><figcaption>Figma · {f['width']}×{f['height']}</figcaption>{img(f['image'],width)}</figure><figure><figcaption>Android · {a.get('widthDp',width)}dp / {esc(a.get('heightDp','—'))}dp</figcaption>{img(a.get('image'),a.get('widthDp',width))}</figure></div><p class="evidence-links"><a href="{esc(f['url'])}" target="_blank">Figma 원본</a> · <a href="2026-10-25-full-screen-audit.html#{id}" target="_blank">전체 보고서의 근거·조건</a></p></section>'''
cards=[]
for n,g in enumerate(groups,1):
 reps=g['representatives'];first=pair(reps[0]);others=''.join(pair(x) for x in reps[1:]);related=' · '.join(f'<a href="2026-10-25-full-screen-audit.html#{x}" target="_blank">{esc(rows[x]["figma"]["nodeId"])}</a>' for x in g['related'])
 sources=''.join(f'<li><a href="../../{esc(path)}" target="_blank">{esc(Path(path).name)}</a></li>' for path in g['code'])
 cards.append(f'''<article id="{g['id']}" data-kind="{g['kind']}" data-search="{esc(g['title'])}"><header><div class="eyebrow">{n:02d} / {g['priority']} <span class="tag {'conditional' if g['kind']=='기준 결정 후' else ''}">{g['kind']}</span></div><h2>{esc(g['title'])}</h2></header><div class="explain"><div><h3>선정 이유</h3>{bullet(g['why'])}</div><div><h3>수정 범위 제안</h3>{bullet(g['scope'])}</div></div><div class="limit"><b>판단할 때 유의할 조건</b><p>{esc(g['limit'])}</p></div>{first}<details class="variants"><summary>나머지 비교 이미지 {len(reps)-1}개 보기</summary>{others}</details><div class="bottom"><h3>수정 후 확인 기준</h3><p>{esc(g['acceptance'])}</p><details><summary>관련 노드와 수정 위치</summary><p>{related}</p><ul>{sources}</ul></details><div class="decision-row"><label>내 결정 <select class="decision" aria-label="{esc(g['title'])} 결정"><option value="pending">미정</option><option value="fix">수정</option><option value="keep">유지</option><option value="later">나중에</option></select></label><textarea class="memo" aria-label="{esc(g['title'])} 메모" placeholder="적용할 기준·수정 범위·우선순위 메모"></textarea></div></div></article>''')
summary=''.join(f'<tr><td><a href="#{g["id"]}">{esc(g["title"])}</a></td><td>{g["kind"]}</td><td>{esc(g["why"][0])}</td></tr>' for g in groups)
excluded=''.join(f'<li><strong>{esc(name)}</strong> — {esc(reason)}</li>' for name,reason in exclusions)
page='''<!doctype html><html lang="ko"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Fillsa UI 수정 후보 요약</title><style>
*{box-sizing:border-box}body{margin:0;background:#eef1f6;color:#243149;font-family:system-ui,-apple-system,sans-serif;line-height:1.65}main{max-width:1160px;margin:auto;padding:30px 22px}a{color:#355b9c}h1{font-size:34px;line-height:1.25;margin:12px 0 20px}h2{font-size:24px;margin:10px 0}h3{font-size:16px;margin:0 0 8px}h4{font-size:15px;margin:0 0 18px}p{margin:8px 0}ul{margin:8px 0;padding-left:22px}li+li{margin-top:7px}.intro,article,.toolbar,.excluded{background:white;border:1px solid #dce2eb;border-radius:14px}.intro,.excluded{padding:28px}.eyebrow{font-size:13px;font-weight:650;color:#61718a;letter-spacing:.04em}.lead{max-width:900px}.stats{display:flex;gap:12px;flex-wrap:wrap;margin:24px 0}.stats div{background:#f1f4fb;padding:13px 22px;border-radius:10px}.stats b{display:block;font-size:28px;color:#244f92}.notice{border-left:4px solid #5975b4;padding:10px 16px;background:#f4f7fc}.notice small{overflow-wrap:anywhere}table{border-collapse:collapse;width:100%;margin:22px 0}th,td{border-bottom:1px solid #e1e6ee;padding:12px 8px;text-align:left;font-size:14px}td:first-child{width:30%}td:nth-child(2){width:110px}.toolbar{display:flex;gap:8px;align-items:center;flex-wrap:wrap;padding:12px;margin:20px 0;position:sticky;top:0;z-index:3;box-shadow:0 3px 10px #24314912}input,select,textarea,button{font:inherit;border:1px solid #b7c3d5;border-radius:7px;padding:8px;background:white}button{background:#263f68;color:white;cursor:pointer}.toolbar input{width:230px}.toolbar small{font-size:12px}article{margin:24px 0;overflow:hidden;scroll-margin-top:100px}article header{padding:24px 28px;background:#f8faff}.tag{display:inline-block;margin-left:10px;background:#e6f1eb;color:#235c3c;padding:3px 9px;border-radius:20px;font-size:12px}.tag.conditional{background:#fff0cc;color:#825708}.explain{display:grid;grid-template-columns:1fr 1fr;gap:28px;padding:24px 28px}.limit{margin:0 28px 18px;padding:14px 18px;background:#fff8e9;border:1px solid #f1e5c7;border-radius:9px;font-size:14px}.evidence{padding:20px 26px;border-top:1px solid #e5eaf1}.evidence small{color:#61718a;font-weight:400;margin-left:8px}.pair{display:grid;grid-template-columns:1fr 1fr;gap:20px;align-items:start}figure{margin:0;min-width:0;text-align:center}figcaption{font-size:13px;min-height:26px;margin-bottom:10px}figure img{max-width:100%;object-fit:contain;box-shadow:0 1px 7px #24314928}.missing{min-height:260px;background:#f0f3f8;display:flex;flex-direction:column;justify-content:center;padding:20px;border:1px dashed #a8b7cb;color:#596e8a}.missing p{font-size:13px}.evidence-links{text-align:center;font-size:13px;margin:16px 0 0}.variants{border-top:1px solid #e5eaf1;padding-top:12px}.variants>summary{margin:0 26px 14px;color:#355b9c;font-weight:600}summary{cursor:pointer}.bottom{padding:22px 28px;background:#f9fbfd;border-top:1px solid #e5eaf1}.decision-row{margin-top:20px}textarea{display:block;width:100%;min-height:70px;margin-top:10px}.excluded{margin-top:30px;font-size:14px}.footer{padding:24px 2px;color:#61718a;font-size:13px}article[hidden]{display:none}
@media(max-width:700px){main{padding:12px}h1{font-size:27px}h2{font-size:21px}.intro,.excluded{padding:20px}.stats{gap:8px}.stats div{padding:9px 14px}.stats b{font-size:23px}.toolbar{position:static}.toolbar input{width:100%}.explain{grid-template-columns:1fr;gap:18px;padding:20px}.limit{margin:0 15px 15px}.pair{gap:10px}.evidence{padding:16px 10px}figcaption{min-height:44px;font-size:11px}.bottom,article header{padding:20px}.missing{min-height:230px;padding:10px}table th:last-child,table td:last-child{display:none}td:first-child{width:auto}article{scroll-margin-top:10px}}
</style></head><body><main><section class="intro"><div class="eyebrow">FILLSA · UI FIX SHORTLIST · 2026.10.25</div><h1>수정이 필요한 화면만<br>추린 검토 보고서</h1><p class="lead">전체 비교 자료에서 <strong>구체적인 UI 차이가 있는 후보</strong>를 다시 골랐습니다. 같은 화면의 중복 상태는 묶고, 대표 이미지와 수정 범위 제안을 함께 제시합니다.</p><div class="stats"><div><b>8</b>검토 묶음</div><div><b>5</b>UI 수정 후보</div><div><b>3</b>기준 결정 후 검토</div><div><b>19</b>대표 비교 항목</div></div><div class="notice"><b>기존 전체 비교 보고서는 보존했습니다.</b><p>이 문서는 별도 파일이며, 이미지도 HTML 안에 포함했습니다. 새 보고서의 결정·메모는 독립적으로 저장됩니다.</p><small>원본 SHA-256: SOURCE_HASH</small><p><a href="2026-10-25-full-screen-audit.html" target="_blank">전체 비교 보고서</a> · <a href="artifacts/2026-10-25-ui-fix-shortlist/selection.json">선정 목록·근거 JSON</a></p></div><h3 style="margin-top:24px">선정 기준</h3><p>색상·자산·문구 정렬·메뉴 구성이 구체적으로 다르고 이미지 또는 코드로 근거를 확인할 수 있는 경우를 우선했습니다. Preview 오류, 샘플 데이터, OS 바·광고·키보드 차이만 있는 항목은 제외했습니다. <strong>수정 승인이 아닌 후보 목록</strong>입니다.</p><table><thead><tr><th>화면 / 검토 항목</th><th>분류</th><th>선정 이유</th></tr></thead><tbody>SUMMARY</tbody></table><p><strong>비교 조건:</strong> Figma는 원본 전체 프레임, Android는 기존 CLI Preview입니다. 같은 가로 dp 기준으로 표시하되 OS 바·광고 제외로 생기는 높이 차이는 결함으로 세지 않습니다. 팝업 dim과 창 그림자는 실제 기기 추가 확인 대상입니다.</p></section><nav class="toolbar"><input id="search" type="search" placeholder="화면 검색" aria-label="화면 검색"><select id="kind" aria-label="후보 분류"><option value="">전체 후보</option><option>수정 후보</option><option>기준 결정 후</option></select><button id="export">결정 JSON 내보내기</button><span id="count"></span><small id="storage"></small></nav>CARDS<section class="excluded"><h2>이번 목록에서 제외한 항목</h2><p>제외는 정상 판정을 뜻하지 않습니다. 확정할 수 있는 수정 근거가 부족하거나 별도 조건이 필요한 항목입니다.</p><ul>EXCLUDED</ul></section><footer class="footer">새 보고서의 선택은 이 브라우저에만 저장됩니다. 원본 보고서·앱·Figma·Notion에는 자동 반영하지 않습니다. 다른 환경으로 전달할 결정은 JSON으로 내보내세요. 소스 파일·원본 보고서 링크는 저장소와 함께 있을 때 사용할 수 있습니다.</footer></main><script>
const storageKey='fillsa-ui-fix-shortlist-20261025';let decisions={};try{decisions=JSON.parse(localStorage.getItem(storageKey)||'{}')}catch(e){document.querySelector('#storage').textContent='브라우저 저장 불가: JSON을 내보내세요.'}
const cards=[...document.querySelectorAll('article')],search=document.querySelector('#search'),kind=document.querySelector('#kind');
for(const c of cards){const d=decisions[c.id]||{};c.querySelector('.decision').value=d.decision||'pending';c.querySelector('.memo').value=d.memo||'';const save=()=>{decisions[c.id]={decision:c.querySelector('.decision').value,memo:c.querySelector('.memo').value};try{localStorage.setItem(storageKey,JSON.stringify(decisions))}catch(e){document.querySelector('#storage').textContent='保存できません。JSONを使用してください。'}};c.querySelector('.decision').addEventListener('change',save);c.querySelector('.memo').addEventListener('input',save)}
function filter(){let n=0;for(const c of cards){c.hidden=(kind.value&&c.dataset.kind!==kind.value)||!c.dataset.search.toLowerCase().includes(search.value.toLowerCase());if(!c.hidden)n++}document.querySelector('#count').textContent=`${n}개 묶음`}search.addEventListener('input',filter);kind.addEventListener('input',filter);filter();
for(const a of document.querySelectorAll('a[href^="#"]'))a.addEventListener('click',()=>{search.value='';kind.value='';filter()});
document.querySelector('#export').onclick=()=>{const blob=new Blob([JSON.stringify({report:'2026-10-25-ui-fix-shortlist',sourceSha256:'SOURCE_HASH',exportedAt:new Date().toISOString(),decisions},null,2)],{type:'application/json'});const a=document.createElement('a');a.href=URL.createObjectURL(blob);a.download='fillsa-ui-fix-shortlist-decisions.json';a.click();setTimeout(()=>URL.revokeObjectURL(a.href),1000)};
</script></body></html>'''
page=page.replace('保存できません。JSONを使用してください。','브라우저 저장 실패: JSON으로 내보내세요.').replace('SOURCE_HASH',EXPECTED_SOURCE).replace('SUMMARY',summary).replace('CARDS','\n'.join(cards)).replace('EXCLUDED',excluded)
DEST.write_text(page)
assert hashlib.sha256(SOURCE.read_bytes()).hexdigest()==EXPECTED_SOURCE
print(f'{DEST}: {len(groups)} groups, {len(all_ids)} representative frames, {DEST.stat().st_size:,} bytes; original preserved')
if __name__=='__main__':pass
