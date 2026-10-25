# Home calendar header assets

- Figma file: `VdFocqyqTgevMVCQxwAQ2X`, header `2929:16228` / dark `3039:28108`.
- Original SVGs downloaded from Figma MCP `get_design_context` on 2026-10-25: left `2929:16230` (16×16), down `2929:16235` / `2929:16239` (12×12), right `2929:16241` (16×16).
- `drawable-xxxhdpi/home_calendar_chevron_*.png` are faithful 4× rasterizations of these SVGs, using Sharp at density 288. SVG geometry and colors are unchanged. Native drawable resources render synchronously in both Compose Preview and the app.
- Inter variable font: https://github.com/google/fonts/tree/main/ofl/inter (`Inter[opsz,wght].ttf`). Used only by the year/month buttons at the Figma 14sp / 20sp / Medium style. License: `Inter-OFL.txt`.
