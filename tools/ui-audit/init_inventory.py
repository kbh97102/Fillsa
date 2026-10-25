# -*- coding: utf-8 -*-
"""Initialize the audit ledger. Run once; subsequent results live in screen-map.json."""
import hashlib
import json
import subprocess
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
OUT = ROOT / 'docs/design-qa/artifacts/2026-10-25-full-screen-audit'
def read(name): return json.loads((OUT / name).read_text())
def write(name, value): (OUT / name).write_text(json.dumps(value, ensure_ascii=False, indent=2) + '\n')

inv = read('figma-inventory.json')
states = {n['id']: n for n in read('figma-states.json')}
exports = {n['nodeId']: n for n in read('figma-exports.json')}
cases = []
for node in inv['screenCandidates'] + inv['standaloneCandidates']:
    ident = node['id']; state = states[ident]; name = state['name']; texts = state['texts']
    sections = ' / '.join(s['name'] for s in node.get('sectionPath', []))
    dark = 'dark' in (sections + name).lower() or ident.startswith('3223:') and int(ident.split(':')[1]) >= 5985
    feature, route, component = '공통 표시 상태', '', 'CommonDialog'
    mapping, reason = 'candidate', '기능·본문·표시 상태로 후보 연결. 이미지 검토 후 확정.'
    if any(s['name'] in ['Apple', 'Google'] for s in node.get('sectionPath', [])):
        feature, mapping, reason = '홍보 자산', 'excluded', '스토어 홍보용 이미지. 앱 화면 대조에서 제외하되 인덱스 보존.'
    elif ident == '2980:967':
        feature, mapping, reason = '디자인 자산', 'component_reference', '캐릭터 원본 자산 모음. 독립 앱 화면이 아님.'
    elif 'onboarding' in name:
        feature = '온보딩'; route = 'Screens.Splash' if 'new' not in name and 'guide' not in name else 'Screens.OnBoardingGuide'
        component = 'SplashContent' if route.endswith('Splash') else 'IntroduceView'
    elif 'login' in name and 'my page' not in name:
        feature, route, component = '로그인', 'Screens.Login', 'LoginContent'
        if 'apple' in name: mapping, reason = 'platform_only', 'Apple 로그인 포함 iOS 변형. Android 대응 여부 별도 검토.'
        if 'modal' in name: route, component = 'Screens.Home', 'CommonDialog'
    elif 'write' in name or ident in ['2929:10996','3025:24578','3025:24618','2988:24099','2988:24127','3025:24232']:
        feature, route, component = '필사 입력·결과', 'Screens.DailyQuote', 'TypingQuoteContent'
        if node['width'] == 320: component = 'TypingResultContent'
    elif 'share' in name:
        feature, route, component = '공유', 'Screens.Share', 'ShareItem' if 'share.img' in name else 'ShareContent'
    elif '이미지' in name or 'img_check' in name:
        feature, route, component = '이미지 등록·보기', 'Screens.Home', 'ImageDialog'
    elif '3. calendar' in sections:
        feature, route, component = 'Calendar', 'Screens.Calendar', 'CalendarContent'
    elif 'my page' in sections:
        feature, route, component = 'My Page·계정', 'Screens.MyPage', 'MyPageContent'
        if 'notice' in name:
            feature, route, component = '공지사항', 'MyPageScreens.Notice', 'NoticeContent'
            if 'detail' in name: route, component = 'MyPageScreens.NoticeDetail', 'NoticeDetailView'
        elif 'theme' in name: feature, component = '테마·알림 설정', 'ThemeDialogContent'
        elif name in ['5-2. inform', 'list-2'] or 'delete ID' in name:
            feature, route, component = '테마·알림 설정', 'MyPageScreens.Alert', 'AlertContent'
    elif '2. home' in sections or '질문' in sections:
        feature, route, component = 'Home', 'Screens.Home', 'FigmaHomeContent'
    if state['type'] == 'COMPONENT_SET': mapping, reason = 'component_reference', '여러 variant를 포함한 컴포넌트 모음. 단일 화면과 비교하지 않음.'
    exp = exports.get(ident, {})
    image = exp.get('path') if exp.get('downloadExit') == 0 else None
    notes = [reason]
    if ident == '2929:17920': notes.append('Light 원본 이름은 write-as is. Dark write-to be와 동일 최종안으로 단정하지 않음.')
    if ident in ['3039:24906','3039:28370']: notes.append('HTML 승인본이라는 Figma 레이어 이름이며 이번 Android 검증 통과를 의미하지 않음.')
    if not image and mapping != 'excluded': notes.append('Figma MCP 사용량 한도로 새 기준 이미지 추출 불가.' if exp.get('error') else '이미지 추출 대상 아님.')
    cases.append(dict(id='figma-'+ident.replace(':','-'),feature=feature,screen=name.replace('\b',''),state=texts,theme='Dark' if dark else 'Light',platform='Figma',
        figma=dict(pageId=inv['pageId'],nodeId=ident,url=f'https://www.figma.com/design/{inv["fileKey"]}?node-id={ident.replace(":","-")}',width=node['width'],height=node['height'],image=image),
        android=dict(route=route,productionComposable=component),fixture=dict(locale='ko',fontScale=1,date=None,text=texts,auth=None),mappingStatus=mapping,
        captureStatus='figma_ready' if image else 'blocked',comparisonStatus='not_reviewed' if image else 'not_comparable',differences=[],decision='pending',notes=notes))

for screen,route,component,feature in [('기록 목록','Screens.QuoteList','QuoteListContent','기록 목록·상세·메모'),('기록 상세','Screens.QuoteDetail','QuoteDetailContent','기록 목록·상세·메모'),('메모 입력','Screens.MemoInsert','MemoInsertContent','기록 목록·상세·메모'),('위젯 설정','WidgetConfig','MyWidgetConfigScreen','위젯·위젯 설정'),('위젯 2×2','Widget2x2','WidgetPreview2x2','위젯·위젯 설정'),('위젯 4×2','Widget4x2','WidgetPreview4x2','위젯·위젯 설정')]:
    for theme in ['Light','Dark']:
        cases.append(dict(id='android-'+component.lower()+'-'+theme.lower(),feature=feature,screen=screen,state='고정 샘플 데이터',theme=theme,platform='Android',figma={},android=dict(route=route,productionComposable=component),fixture=dict(locale='ko',fontScale=1),mappingStatus='android_only',captureStatus='pending',comparisonStatus='not_comparable',differences=[],decision='pending',notes=['지정 Figma 페이지에서 대응 화면을 찾지 못함. 기존 다른 페이지 디자인을 자동 사용하지 않음.']))
for key,title in [('year-month','연도·월 시스템 선택창'),('keyboard','IME 입력 키보드'),('photo-picker','시스템 사진 선택기'),('share-sheet','시스템 공유창'),('launcher-widget','런처 Glance 위젯'),('permission','알림·정확한 알람 권한')]:
    cases.append(dict(id='runtime-'+key,feature='플랫폼 화면',screen=title,state='런타임 검증 필요',theme='System',platform='Android',figma={},android={},fixture={},mappingStatus='platform_only',captureStatus='runtime_required',comparisonStatus='not_comparable',differences=[],decision='pending',notes=['Compose Preview로 시스템 화면을 대체할 수 없음. 실제 기기·에뮬레이터 증거가 필요.']))
write('screen-map.json',cases)
files = sorted(p for module in ['app','presentation','domain','data'] for p in (ROOT/module/'src').rglob('*') if p.is_file() and p.suffix in ['.kt','.xml','.json','.svg','.png'])
hashes = {str(p.relative_to(ROOT)):hashlib.sha256(p.read_bytes()).hexdigest() for p in files}
write('source-baseline.json',dict(head=subprocess.check_output(['git','rev-parse','HEAD'],cwd=ROOT,text=True).strip(),files=hashes,sha256=hashlib.sha256(json.dumps(hashes,sort_keys=True).encode()).hexdigest()))
print(f'{len(cases)} ledger rows; {sum(bool(c["figma"].get("image")) for c in cases)} Figma PNGs')
