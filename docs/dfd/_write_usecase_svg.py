# -*- coding: utf-8 -*-
"""Emit UTF-8 SVG with Chinese text (avoids tool encoding issues)."""
from pathlib import Path

def main() -> None:
    out = Path(__file__).with_name("usecase-子系统一-多校预约与时空调度.svg")
    # Unicode escapes keep this source file ASCII-only
    svg = r'''<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1180 920" width="1180" height="920">
  <title>Use Case Diagram Subsystem 1</title>
  <style>
    .title { font: bold 18px "Microsoft YaHei", "PingFang SC", sans-serif; fill: #1a1a2e; }
    .sub { font: 13px "Microsoft YaHei", sans-serif; fill: #4a5568; }
    .sysbox { fill: #f7fafc; stroke: #2d3748; stroke-width: 2; stroke-dasharray: 8 4; }
    .actorbox { fill: #edf2f7; stroke: #2b6cb0; stroke-width: 1.8; }
    .actort { font: 13px "Microsoft YaHei", sans-serif; fill: #1a202c; text-anchor: middle; }
    .extbox { fill: #fffaf0; stroke: #c05621; stroke-width: 1.5; stroke-dasharray: 5 3; }
    .extt { font: 12px "Microsoft YaHei", sans-serif; fill: #744210; text-anchor: middle; }
    .uc { fill: #ffffff; stroke: #2f855a; stroke-width: 1.6; }
    .uct { font: 12px "Microsoft YaHei", sans-serif; fill: #1a202c; text-anchor: middle; }
    .ucid { font: 11px "Microsoft YaHei", sans-serif; fill: #4a5568; text-anchor: middle; }
    .line { stroke: #2d3748; stroke-width: 1.4; fill: none; }
    .lineaux { stroke: #718096; stroke-width: 1.2; stroke-dasharray: 6 4; fill: none; }
    .note { font: 11px "Microsoft YaHei", sans-serif; fill: #4a5568; }
    .small { font: 10px "Microsoft YaHei", sans-serif; fill: #4a5568; text-anchor: middle; }
  </style>
  <rect width="100%" height="100%" fill="#fafafa"/>
  <text x="590" y="36" text-anchor="middle" class="title">''' + "\u5b50\u7cfb\u7edf\u4e00\uff1a\u591a\u6821\u4f53\u80b2\u573a\u5730\u9884\u7ea6\u4e0e\u65f6\u7a7a\u8c03\u5ea6" + r'''</text>
  <text x="590" y="56" text-anchor="middle" class="title">''' + "\u7528\u4f8b\u56fe" + r'''</text>
  <text x="590" y="78" text-anchor="middle" class="sub">''' + "\u5178\u578b\u7528\u4f8b UC-A \u81f3 UC-E\uff0c\u89d2\u8272\u4e0e\u5916\u90e8\u534f\u4f5c" + r'''</text>

  <rect x="48" y="120" width="108" height="52" rx="6" class="actorbox"/>
  <text x="102" y="152" class="actort">''' + "\u5b66\u751f" + r'''</text>
  <rect x="48" y="220" width="108" height="52" rx="6" class="actorbox"/>
  <text x="102" y="252" class="actort">''' + "\u6559\u5e08" + r'''</text>
  <rect x="48" y="320" width="128" height="56" rx="6" class="actorbox"/>
  <text x="112" y="348" class="actort">''' + "\u573a\u5730\u7ba1\u7406\u5458" + r'''</text>
  <text x="112" y="366" class="small">''' + "\u53ef\u9009" + r'''</text>

  <rect x="220" y="96" width="640" height="780" rx="12" class="sysbox"/>
  <text x="540" y="124" text-anchor="middle" class="title" style="font-size:15px;">''' + "\u591a\u6821\u4f53\u80b2\u573a\u5730\u9884\u7ea6\u4e0e\u65f6\u7a7a\u8c03\u5ea6\u7cfb\u7edf" + r'''</text>

  <ellipse cx="540" cy="200" rx="168" ry="38" class="uc"/>
  <text x="540" y="194" class="ucid">UC-A</text>
  <text x="540" y="212" class="uct">''' + "\u6559\u5e08\u5b66\u671f\u5360\u573a" + r'''</text>
  <ellipse cx="540" cy="300" rx="168" ry="38" class="uc"/>
  <text x="540" y="294" class="ucid">UC-B</text>
  <text x="540" y="312" class="uct">''' + "\u5b66\u751f\u8bfe\u5916\u9884\u7ea6" + r'''</text>
  <ellipse cx="540" cy="400" rx="168" ry="38" class="uc"/>
  <text x="540" y="394" class="ucid">UC-C</text>
  <text x="540" y="412" class="uct">''' + "\u4e34\u65f6\u95ed\u9986" + r'''</text>
  <ellipse cx="540" cy="500" rx="188" ry="40" class="uc"/>
  <text x="540" y="492" class="ucid">UC-D</text>
  <text x="540" y="510" class="uct">''' + "\u9996\u9875\u5c31\u8fd1\u7a7a\u95f2\u63a8\u8350" + r'''</text>
  <ellipse cx="540" cy="600" rx="168" ry="38" class="uc"/>
  <text x="540" y="594" class="ucid">UC-E</text>
  <text x="540" y="612" class="uct">''' + "\u5bfc\u51fa\u5bf9\u8d26 CSV" + r'''</text>
  <ellipse cx="540" cy="700" rx="200" ry="42" class="uc"/>
  <text x="540" y="688" class="ucid">UC-F</text>
  <text x="540" y="706" class="uct">''' + "\u4f7f\u7528\u60c5\u51b5\u7ba1\u7406\u4e0e\u7edf\u8ba1" + r'''</text>
  <text x="540" y="722" class="small">''' + "\u7518\u7279\u5217\u8868\u3001\u5360\u7528\u7387\u3001\u7b5b\u9009" + r'''</text>
  <ellipse cx="540" cy="810" rx="188" ry="38" class="uc"/>
  <text x="540" y="804" class="ucid">UC-G</text>
  <text x="540" y="822" class="uct">''' + "\u914d\u7f6e\u63d0\u9192\u4e0e\u9690\u79c1\u504f\u597d" + r'''</text>

  <path d="M 156 246 L 372 200" class="line"/>
  <path d="M 156 146 L 372 300" class="line"/>
  <path d="M 156 146 L 350 500" class="line"/>
  <path d="M 176 348 L 380 400" class="line"/>
  <path d="M 176 348 L 380 600" class="line"/>
  <path d="M 176 348 L 380 700" class="line"/>
  <path d="M 156 246 L 400 700" class="line"/>
  <path d="M 156 146 L 400 810" class="line"/>

  <rect x="930" y="140" width="120" height="48" rx="6" class="extbox"/>
  <text x="990" y="164" class="extt">''' + "\u5b50\u7cfb\u7edf\u4e8c" + r'''</text>
  <text x="990" y="180" class="small">''' + "\u573a\u5730\u53ef\u7ea6" + r'''</text>
  <rect x="930" y="220" width="120" height="48" rx="6" class="extbox"/>
  <text x="990" y="244" class="extt">''' + "\u5b50\u7cfb\u7edf\u4e09" + r'''</text>
  <text x="990" y="260" class="small">''' + "\u52a9\u624b API" + r'''</text>
  <rect x="930" y="300" width="120" height="44" rx="6" class="extbox"/>
  <text x="990" y="328" class="extt">''' + "\u7edf\u4e00\u8eab\u4efd\u8ba4\u8bc1" + r'''</text>
  <rect x="930" y="380" width="120" height="48" rx="6" class="extbox"/>
  <text x="990" y="404" class="extt">''' + "\u5730\u56fe\u670d\u52a1" + r'''</text>
  <text x="990" y="420" class="small">''' + "\u53ef\u9009" + r'''</text>

  <path d="M 708 300 L 930 168" class="lineaux"/>
  <text x="800" y="220" class="note">''' + "\u53ef\u7ea6\u72b6\u6001" + r'''</text>
  <path d="M 708 500 L 930 406" class="lineaux"/>
  <text x="800" y="440" class="note">''' + "\u8ddd\u79bb\u6392\u5e8f" + r'''</text>
  <path d="M 708 810 L 930 248" class="lineaux"/>
  <text x="800" y="520" class="note">''' + "\u67e5\u8be2\u63a5\u53e3" + r'''</text>
  <path d="M 156 146 L 930 322" class="lineaux"/>
  <path d="M 156 246 L 930 322" class="lineaux"/>
  <path d="M 176 348 L 930 322" class="lineaux"/>

  <text x="590" y="902" text-anchor="middle" class="note">''' + "FR-5 \u6838\u9a8c\u4e0e FR-9 \u884c\u4e3a\u6570\u636e\u53ef\u5728\u8be6\u7ec6\u8bbe\u8ba1\u4e2d\u4ee5\u6269\u5c55\u7528\u4f8b\u7ec6\u5316" + r'''</text>
</svg>
'''
    out.write_text(svg, encoding="utf-8", newline="\n")
    print("Wrote", out, "bytes", len(svg.encode("utf-8")))


if __name__ == "__main__":
    main()
