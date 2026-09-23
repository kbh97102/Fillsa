# Global Loading Spinner Implementation Plan

> **For agentic workers:** Execute this plan task-by-task in the current session. TDD steps are intentionally omitted per user request; every UI task still requires deterministic Compose Preview and build verification.

**Goal:** Figma `2929:6086`과 동일한 전역 로딩 오버레이를 구현하고, 화면 진입 중 실제 API/Paging 대기 시간에 표시한다.

**Architecture:** `MainActivity`의 단일 root overlay는 유지하되, 여러 화면·Paging source가 동시에 로딩을 등록해도 먼저 끝난 요청이 overlay를 숨기지 않도록 owner 기반 `GlobalLoadingState`를 둔다. `BaseViewModel`은 API 결과를 받은 뒤 로딩을 켜는 현재 구조를 분리하여 `withLoading { apiCall() }`이 요청 실행 전후만 책임지고, `getResponse(result)`는 성공/실패 해석만 담당한다.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Coil SVG, Navigation Compose, Paging 3, Android CLI Compose Preview

**Spec:** `docs/screens/common.md`

## Global Constraints

- UI 기준은 Figma file `VdFocqyqTgevMVCQxwAQ2X`, node `2929:6086`이다.
- dim은 `#212121` 80%, spinner frame은 120dp, SVG visual은 112dp다.
- Figma에서 받은 spinner SVG 원본 bytes를 사용하고 새 벡터를 직접 그리지 않는다.
- 화면 전환/진입 API와 Paging refresh만 blocking overlay 대상이다.
- 좋아요, 답변 저장, 이미지 업로드/삭제, 메모 저장, 광고/팝업/위젯 refresh는 blocking overlay 대상에서 제외한다.
- TDD와 신규 단위 테스트는 사용자 요청에 따라 제외한다.
- 컴포넌트 Preview는 중간 증거이며 최종 승인은 에뮬레이터 전체 프레임으로 확인한다.

## Review Focus

- 두 API가 겹치면 하나가 먼저 끝나도 나머지가 끝날 때까지 overlay가 유지되어야 한다.
- API 실패 또는 coroutine 취소 시 owner와 request count가 반드시 해제되어야 한다.
- 로딩 중 route가 dispose되면 이전 화면 owner가 남아 overlay가 고착되지 않아야 한다.
- Paging append는 목록 하단 로딩으로 남기고 최초/새로고침 `refresh`만 전역 overlay를 사용해야 한다.
- background/mutation 요청은 화면 전체 입력을 막지 않아야 한다.

---

### Task 1: Figma asset 및 QA 기준 고정

**Files:**
- Create: `presentation/src/main/assets/figma/common/progress_indicator_01.svg`
- Create: `docs/design-qa/2026-09-21-android-global-loading-spinner-qa.md`
- Existing reference: `docs/design-qa/assets/global-loading-spinner-2929-6086/reference-full.png`
- Modify: `docs/screens/common.md`

**Interfaces:**
- Consumes: Figma nodes `2929:6175`, `2929:6176`, exported spinner SVG.
- Produces: exact local asset path `file:///android_asset/figma/common/progress_indicator_01.svg` and QA evidence contract.

- [x] **Step 1: Add the exact exported SVG**

```xml
<svg width="112" height="112" viewBox="0 0 112 112" fill="none" xmlns="http://www.w3.org/2000/svg">
  <circle cx="56" cy="56" r="50" stroke="#D3D5FF" stroke-width="12"/>
  <path d="M56 6C39.6418 6 25.1184 13.8555 15.9963 26C9.71945 34.3566 6 44.7439 6 56C6 83.6142 28.3858 106 56 106C68.1724 106 79.329 101.65 88 94.4204" stroke="#5C65FF" stroke-width="12" stroke-linecap="round"/>
</svg>
```

- [x] **Step 2: Record the QA contract**

Document the 360 × 720 reference, dim color/opacity, 120dp frame, 112dp visual, Preview command/output, runtime target, and non-final status until runtime capture.

- [x] **Step 3: Verify the stored references**

Run:

```bash
sips -g pixelWidth -g pixelHeight docs/design-qa/assets/global-loading-spinner-2929-6086/reference-full.png
```

Expected: `pixelWidth: 360`, `pixelHeight: 720`.

### Task 2: Figma 전역 spinner 컴포넌트 구현

**Files:**
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/common/CircleLoadingSpinner.kt`
- Create output: `docs/design-qa/artifacts/2026-09-21-global-loading-spinner/preview.png`

**Interfaces:**
- Consumes: `CircleLoadingSpinner(isLoading: Boolean)` and the exact SVG from Task 1.
- Produces: same public composable signature, blocking full-screen overlay, deterministic `GlobalLoadingSpinnerPreview`.

- [x] **Step 1: Replace the current Material spinner visual**

Keep the public signature and implement these exact layers:

```kotlin
Box(
    Modifier
        .fillMaxSize()
        .background(Color(0xFF212121).copy(alpha = 0.8f))
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) awaitPointerEvent().changes.forEach { it.consume() }
            }
        }
        .semantics {
            contentDescription = "로딩 중"
            progressBarRangeInfo = ProgressBarRangeInfo.Indeterminate
        },
    contentAlignment = Alignment.Center,
) {
    Box(Modifier.size(120.dp), contentAlignment = Alignment.Center) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("file:///android_asset/figma/common/progress_indicator_01.svg")
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = null,
            modifier = Modifier.size(112.dp).graphicsLayer { rotationZ = rotation },
        )
    }
}
```

Use `rememberInfiniteTransition` with linear `0f..360f` rotation and a 1,000ms restart cycle. Do not change the public call site.

- [x] **Step 2: Add deterministic Preview**

Add `@Preview(widthDp = 360, heightDp = 720, locale = "ko", fontScale = 1f)` with a light fixture background and `isLoading = true`.

- [x] **Step 3: Render and compare the component**

Run:

```bash
~/.local/bin/android studio render-compose-preview \
  presentation/src/main/java/com/arakene/presentation/ui/common/CircleLoadingSpinner.kt \
  GlobalLoadingSpinnerPreview \
  --project Fillsa \
  --output-image-file=docs/design-qa/artifacts/2026-09-21-global-loading-spinner/preview.png \
  --print-semantics
```

Expected: 360 × 720 root, 80% dim, centered 120dp frame/112dp visual, indeterminate progress semantics.

### Task 3: 전역 로딩 owner 결합

**Files:**
- Create: `presentation/src/main/java/com/arakene/presentation/util/GlobalLoadingState.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/util/ComposeExtension.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/common/WithBaseErrorHandling.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/MainActivity.kt`

**Interfaces:**
- Produces: `GlobalLoadingState.set(owner: Any, loading: Boolean)`, `GlobalLoadingState.isLoading: Boolean`, `BindGlobalLoading(isLoading: Boolean)`.
- Consumes: every `BaseViewModel.isLoading` and Paging refresh state from Task 5.

- [x] **Step 1: Add owner-based state**

```kotlin
@Stable
class GlobalLoadingState {
    private val owners = mutableStateMapOf<Any, Unit>()
    val isLoading: Boolean get() = owners.isNotEmpty()

    fun set(owner: Any, loading: Boolean) {
        if (loading) owners[owner] = Unit else owners.remove(owner)
    }
}
```

- [x] **Step 2: Replace the Boolean flow CompositionLocal**

Change `LocalLoadingState` to `compositionLocalOf<GlobalLoadingState>` and add:

```kotlin
@Composable
fun BindGlobalLoading(isLoading: Boolean) {
    val global = LocalLoadingState.current
    val owner = remember { Any() }
    LaunchedEffect(isLoading) { global.set(owner, isLoading) }
    DisposableEffect(owner) { onDispose { global.set(owner, false) } }
}
```

- [x] **Step 3: Bind every routed BaseViewModel in one place**

In `WithBaseErrorHandling`, collect `viewModel.isLoading` and call `BindGlobalLoading(isLoading)`. Remove the duplicate global mutation from `rememberBaseViewModel`; it should only return `hiltViewModel()`.

- [x] **Step 4: Update MainActivity root**

Replace `MutableStateFlow(false)` and `collectAsState()` with `remember { GlobalLoadingState() }`, provide it through `LocalLoadingState`, and call:

```kotlin
CircleLoadingSpinner(isLoading = globalLoadingState.isLoading)
```

### Task 4: API 실행 전후 로딩 경계 수정

**Files:**
- Modify: `presentation/src/main/java/com/arakene/presentation/util/BaseViewModel.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/viewmodel/LoginViewModel.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/viewmodel/HomeViewModel.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/viewmodel/TypingViewModel.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/viewmodel/CalendarViewModel.kt`

**Interfaces:**
- Produces: `protected suspend fun <T> withLoading(block: suspend () -> T): T`.
- Preserves: `protected suspend fun <T> getResponse(response: ApiResult<T>): T?` as result/error mapping only.

- [x] **Step 1: Separate request timing from response mapping**

Implement a request counter confined to `viewModelScope` callers:

```kotlin
private var activeLoadingRequests = 0

protected suspend fun <T> withLoading(block: suspend () -> T): T {
    activeLoadingRequests += 1
    _isLoading.value = true
    return try {
        block()
    } finally {
        activeLoadingRequests = (activeLoadingRequests - 1).coerceAtLeast(0)
        _isLoading.value = activeLoadingRequests > 0
    }
}
```

Remove loading mutation and the ineffective delayed re-set from `getResponse`; keep success/failure mapping and `_error.emit` unchanged.

- [x] **Step 2: Wrap only blocking transition requests**

Use these call shapes:

```kotlin
val apiResult = withLoading { getMemberWeeklyQuotesUseCase(endDate) }
val result = getResponse(apiResult)
```

Apply to:

- Login submit: `loginUseCase`.
- Home entry/resume/window boundary: `getMemberWeeklyQuotesUseCase`, guest `getDailyQuoteNoTokenUseCase`.
- Typing entry: member `getTypingUseCase`.
- Calendar entry/month change: member `getQuotesMonthlyUseCase`, guest `getMonthlyQuotesNonMemberUseCase`.

Preserve auth/request revision checks between the raw API result and `getResponse` where they already exist.

- [x] **Step 3: Keep mutations non-blocking**

Migrate existing `getResponse(..., useLoading = false)` calls to plain `getResponse(result)`. Do not wrap answer, like, upload/delete, memo, background refresh, ad, popup, streak, or widget requests with `withLoading`.

- [x] **Step 4: Confirm no late-loading call remains**

Run:

```bash
rg -n "getResponse\([^)]*UseCase|getResponse\(apiResult|useLoading" presentation/src/main/java/com/arakene/presentation/viewmodel
```

Expected: no transition API is evaluated directly as a `getResponse` argument; no `useLoading` argument remains.

### Task 5: Paging 화면 진입 로딩 연결

**Files:**
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/quotelist/QuoteListView.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/mypage/NoticeView.kt`

**Interfaces:**
- Consumes: `BindGlobalLoading(isLoading: Boolean)` from Task 3.
- Produces: global overlay only for `paging.loadState.refresh is LoadState.Loading`.

- [x] **Step 1: Bind QuoteList refresh**

```kotlin
BindGlobalLoading(paging.loadState.refresh is LoadState.Loading)
```

Place it immediately after `collectAsLazyPagingItems()`. Do not bind append/prepend.

- [x] **Step 2: Bind Notice refresh**

Apply the same refresh-only binding immediately after `viewModel.getNotice.collectAsLazyPagingItems()`.

- [x] **Step 3: Preserve paging errors and content**

Keep `HandlePagingError`, empty-state logic, list rendering, and Paging retry/refresh behavior unchanged.

### Task 6: Build 및 runtime QA

**Files:**
- Update: `docs/design-qa/2026-09-21-android-global-loading-spinner-qa.md`
- Create output: `docs/design-qa/artifacts/2026-09-21-global-loading-spinner/runtime-home.png`

**Interfaces:**
- Consumes: completed Tasks 1–5.
- Produces: Preview evidence, build evidence, full-frame runtime comparison, remaining-difference record.

- [x] **Step 1: Verify compilation and assembly**

```bash
./gradlew :presentation:compileDebugKotlin :app:assembleDebug --console=plain
git diff --check
```

Expected: `BUILD SUCCESSFUL`, no diff whitespace errors.

- [x] **Step 2: Verify runtime behavior**

On a 360 × 720dp light-mode emulator, throttle or delay a Home entry API, navigate into Home, and capture the full frame while the request is active. Confirm dim/position against `reference-full.png` and confirm taps do not reach the underlying screen.

- [ ] **Step 3: Verify lifecycle and overlap cases** — 사용자 직접 확인 대기

Manually confirm:

1. Navigate away during a pending request: overlay clears or transfers to the new screen owner.
2. Trigger overlapping blocking requests: overlay remains until both finish.
3. Fail/cancel a request: overlay clears and existing error UI appears.
4. Load Paging append: no full-screen overlay.
5. Trigger like/answer/image/memo/background API: no full-screen overlay.

- [x] **Step 4: Record final status accurately**

Mark `Pass` only after the full runtime frame matches Figma. If network throttling or target state cannot be captured, record `Blocked` with the exact missing evidence.

## Self-review

- Spec coverage: exact Figma asset, dim, size, animation, input blocking, API timing, routed ViewModel binding, Paging refresh, runtime evidence are assigned.
- Placeholder scan: no placeholder implementation steps remain.
- Type consistency: `GlobalLoadingState`, `BindGlobalLoading`, `withLoading`, and existing `getResponse` signatures are consistent across tasks.
- TDD exception: no test-first steps are included; Preview, build, lifecycle, overlap, and runtime checks replace them per user request.
