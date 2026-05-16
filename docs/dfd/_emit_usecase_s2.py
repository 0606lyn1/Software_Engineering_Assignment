# -*- coding: utf-8 -*-
"""Emit UTF-8 usecase-subsystem-2-operations.svg (Unicode via codepoints in source).

Checked-in SVG may use XML numeric character references for CJK; run this script
to regenerate plain UTF-8 text nodes if you prefer raw Chinese in the file.
"""
from pathlib import Path

def main() -> None:
    Z = chr  # short

    def u(*codes):
        return "".join(chr(c) for c in codes)

    # Common Chinese fragments as Unicode codepoints (decimal)
    svg = r'''<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1320 900" width="1320" height="900">
  <title>Subsystem 2 Use Case</title>
  <style>
    .title { font: bold 18px "Microsoft YaHei", "PingFang SC", sans-serif; fill: #1a1a2e; }
    .sub { font: 13px "Microsoft YaHei", sans-serif; fill: #4a5568; }
    .sysbox { fill: #f0f9ff; stroke: #1e3a5f; stroke-width: 2; stroke-dasharray: 8 4; }
    .actorbox { fill: #e0f2fe; stroke: #0369a1; stroke-width: 1.8; }
    .actort { font: 13px "Microsoft YaHei", sans-serif; fill: #0c4a6e; text-anchor: middle; }
    .extbox { fill: #fff7ed; stroke: #c2410c; stroke-width: 1.5; stroke-dasharray: 5 3; }
    .extt { font: 12px "Microsoft YaHei", sans-serif; fill: #7c2d12; text-anchor: middle; }
    .uc { fill: #ffffff; stroke: #0f766e; stroke-width: 1.6; }
    .ucsub { fill: #ecfdf5; stroke: #047857; stroke-width: 1.4; }
    .uct { font: 12px "Microsoft YaHei", sans-serif; fill: #1a202c; text-anchor: middle; }
    .ucid { font: 11px "Microsoft YaHei", sans-serif; fill: #4a5568; text-anchor: middle; }
    .line { stroke: #1e3a5f; stroke-width: 1.4; fill: none; marker-end: url(#s2mA); }
    .lineaux { stroke: #64748b; stroke-width: 1.2; stroke-dasharray: 6 4; fill: none; marker-end: url(#s2mG); }
    .lineInc { stroke: #7c3aed; stroke-width: 1.35; stroke-dasharray: 5 4; fill: none; marker-end: url(#s2mI); }
    .lineExt { stroke: #c2410c; stroke-width: 1.35; stroke-dasharray: 5 4; fill: none; marker-end: url(#s2mE); }
    .note { font: 11px "Microsoft YaHei", sans-serif; fill: #4a5568; }
    .small { font: 10px "Microsoft YaHei", sans-serif; fill: #4a5568; text-anchor: middle; }
    .rel { font: bold 10px "Microsoft YaHei", sans-serif; fill: #1d4ed8; text-anchor: middle; paint-order: stroke fill; stroke: #fff; stroke-width: 2px; }
    .relData { font: 9px "Microsoft YaHei", sans-serif; fill: #475569; text-anchor: middle; paint-order: stroke fill; stroke: #fff; stroke-width: 2px; }
    .stInc { font: 10px "Microsoft YaHei", sans-serif; fill: #6d28d9; text-anchor: middle; font-weight: bold; }
    .stExt { font: 10px "Microsoft YaHei", sans-serif; fill: #c2410c; text-anchor: middle; font-weight: bold; }
  </style>
  <defs>
    <marker id="s2mA" markerWidth="10" markerHeight="8" refX="9" refY="4" orient="auto"><polygon points="0 0, 10 4, 0 8" fill="#1e3a5f"/></marker>
    <marker id="s2mG" markerWidth="10" markerHeight="8" refX="9" refY="4" orient="auto"><polygon points="0 0, 10 4, 0 8" fill="#64748b"/></marker>
    <marker id="s2mI" markerWidth="10" markerHeight="8" refX="9" refY="4" orient="auto"><polygon points="0 0, 10 4, 0 8" fill="#7c3aed"/></marker>
    <marker id="s2mE" markerWidth="10" markerHeight="8" refX="9" refY="4" orient="auto"><polygon points="0 0, 10 4, 0 8" fill="#c2410c"/></marker>
  </defs>
  <rect width="100%" height="100%" fill="#fafafa"/>
''' + f'''  <text x="660" y="34" text-anchor="middle" class="title">{u(0x5b50,0x7cfb,0x7edf,0x4e8c,0xff1a,0x573a,0x5730,0x8fd0,0x7ef4,0x3001,0x8d44,0x4ea7,0x4e0e,0x516c,0x4f17,0x76d1,0x7763,0x2014,0x2014,0x7528,0x4f8b,0x56fe,0xff08,0x5b8c,0x6574,0x7248,0xff09)}</text>
  <text x="660" y="56" text-anchor="middle" class="sub">{u(0x5173,0x8054,0x00b7,0x4f9d,0x8d56,0x00b7,0x300c,0x5305,0x542b,0x300d,0x00b7,0x300c,0x6269,0x5c55,0x300d,0xff08,0x46,0x52,0x2d,0x4f,0x31,0xff5e,0x4f,0x31,0x30,0xff09)}</text>

  <rect x="40" y="110" width="100" height="44" rx="6" class="actorbox"/>
  <text x="90" y="138" class="actort">{u(0x5b66,0x751f)}</text>
  <rect x="40" y="180" width="100" height="44" rx="6" class="actorbox"/>
  <text x="90" y="208" class="actort">{u(0x6559,0x5e08)}</text>
  <rect x="32" y="250" width="116" height="48" rx="6" class="actorbox"/>
  <text x="90" y="280" class="actort">{u(0x4fdd,0x6d01,0x8fd0,0x7ef4)}</text>
  <rect x="32" y="330" width="116" height="48" rx="6" class="actorbox"/>
  <text x="90" y="360" class="actort">{u(0x573a,0x5730,0x8d1f,0x8d23,0x4eba)}</text>
  <rect x="32" y="420" width="116" height="48" rx="6" class="actorbox"/>
  <text x="90" y="450" class="actort">{u(0x7ba1,0x7406,0x5458)}</text>

  <rect x="200" y="78" width="1000" height="780" rx="12" class="sysbox"/>
  <text x="700" y="108" text-anchor="middle" class="title" style="font-size:15px;">{u(0x573a,0x5730,0x8fd0,0x7ef4,0x3001,0x8d44,0x4ea7,0x4e0e,0x516c,0x4f17,0x76d1,0x7763,0x7cfb,0x7edf)}</text>
''' + r'''
  <ellipse cx="480" cy="190" rx="175" ry="38" class="uc"/>
  <text x="480" y="184" class="ucid">UC-O1</text>
''' + f'''  <text x="480" y="202" class="uct">{u(0x6e05,0x6d01,0x7ef4,0x62a4,0x53f0,0x8d26)}</text>
  <ellipse cx="480" cy="300" rx="175" ry="38" class="uc"/>
  <text x="480" y="294" class="ucid">UC-O2</text>
  <text x="480" y="312" class="uct">{u(0x8d23,0x4efb,0x4eba,0x4fe1,0x606f)}</text>
  <ellipse cx="480" cy="410" rx="175" ry="38" class="uc"/>
  <text x="480" y="404" class="ucid">UC-O3</text>
  <text x="480" y="422" class="uct">{u(0x8bc4,0x8bba,0x4e0e,0x4e3e,0x62a5,0x5904,0x7f6e)}</text>
  <ellipse cx="480" cy="520" rx="175" ry="38" class="uc"/>
  <text x="480" y="514" class="ucid">UC-O4</text>
  <text x="480" y="532" class="uct">{u(0x706f,0x5149,0x7ba1,0x7406)}</text>
  <ellipse cx="480" cy="630" rx="175" ry="38" class="uc"/>
  <text x="480" y="624" class="ucid">UC-O5</text>
  <text x="480" y="642" class="uct">{u(0x7528,0x5177,0x4e0e,0x81ea,0x5e26,0x8bf4,0x660e)}</text>
  <ellipse cx="480" cy="750" rx="195" ry="40" class="uc"/>
  <text x="480" y="742" class="ucid">UC-O6</text>
  <text x="480" y="760" class="uct">{u(0x53ef,0x7ea6,0x6027,0x8f93,0x51fa,0x4e0e,0x540c,0x6b65)}</text>
''' + r'''
  <ellipse cx="900" cy="190" rx="125" ry="34" class="ucsub"/>
  <text x="900" y="184" class="ucid">UC-O1a</text>
''' + f'''  <text x="900" y="202" class="uct">{u(0x6807,0x51c6,0x767b,0x8bb0,0x4e0e,0x68c0,0x67e5,0x9879)}</text>
  <ellipse cx="900" cy="280" rx="125" ry="34" class="ucsub"/>
  <text x="900" y="274" class="ucid">UC-O1b</text>
  <text x="900" y="292" class="uct">{u(0x8d1f,0x8d23,0x4eba,0x590d,0x6838,0x5f85,0x529e)}</text>
  <ellipse cx="900" cy="400" rx="125" ry="34" class="ucsub"/>
  <text x="900" y="394" class="ucid">UC-O3a</text>
  <text x="900" y="412" class="uct">{u(0x5185,0x5bb9,0x5ba1,0x6838,0x4e0e,0x5904,0x7f6e)}</text>
  <ellipse cx="900" cy="520" rx="125" ry="34" class="ucsub"/>
  <text x="900" y="514" class="ucid">UC-O4a</text>
  <text x="900" y="532" class="uct">{u(0x9884,0x7ea6,0x8054,0x52a8,0x5f00,0x706f)}</text>
  <ellipse cx="900" cy="640" rx="125" ry="36" class="ucsub"/>
  <text x="900" y="632" class="ucid">UC-O4b</text>
  <text x="900" y="650" class="uct">{u(0x7f51,0x5173,0x56de,0x8bfb,0x4e0e,0x544a,0x8b66)}</text>
  <text x="900" y="664" class="small">{u(0x6269,0x5c55,0xb7,0x53ef,0x9009)}</text>
''' + r'''
  <path d="M 655 190 L 775 190" class="lineInc"/>
  <text x="708" y="178" class="stInc">«include»</text>
  <path d="M 655 410 L 775 400" class="lineInc"/>
  <text x="705" y="398" class="stInc">«include»</text>
  <path d="M 655 520 L 775 520" class="lineInc"/>
  <text x="708" y="508" class="stInc">«include»</text>
  <path d="M 775 280 L 655 205" class="lineExt"/>
  <text x="700" y="248" class="stExt">«extend»</text>
  <path d="M 775 640 L 655 545" class="lineExt"/>
  <text x="700" y="600" class="stExt">«extend»</text>

  <path d="M 140 132 L 305 410" class="line"/>
  <path d="M 140 202 L 305 410" class="line"/>
  <path d="M 140 132 L 305 630" class="line"/>
  <path d="M 140 202 L 305 630" class="line"/>
  <path d="M 148 274 L 305 190" class="line"/>
  <path d="M 148 354 L 305 190" class="line"/>
  <path d="M 148 354 L 305 300" class="line"/>
  <path d="M 148 354 L 305 410" class="line"/>
  <path d="M 148 444 L 305 300" class="line"/>
  <path d="M 148 444 L 305 410" class="line"/>
  <path d="M 148 444 L 305 520" class="line"/>
  <path d="M 148 444 L 305 630" class="line"/>
  <path d="M 148 444 L 305 750" class="line"/>
''' + f'''
  <text x="198" y="288" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
  <text x="198" y="318" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
  <text x="198" y="400" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
  <text x="198" y="428" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
  <text x="208" y="228" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
  <text x="218" y="268" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
  <text x="218" y="318" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
  <text x="218" y="368" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
  <text x="218" y="380" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
  <text x="218" y="430" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
  <text x="218" y="480" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
  <text x="218" y="560" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
  <text x="218" y="620" class="rel">{u(0x00ab,0x5173,0x8054,0x00bb)}</text>
''' + r'''
  <rect x="1120" y="150" width="120" height="44" rx="6" class="extbox"/>
''' + f'''  <text x="1180" y="178" class="extt">{u(0x5b50,0x7cfb,0x7edf,0x4e00)}</text>
  <rect x="1120" y="220" width="120" height="44" rx="6" class="extbox"/>
  <text x="1180" y="248" class="extt">{u(0x5b50,0x7cfb,0x7edf,0x4e09)}</text>
  <rect x="1120" y="290" width="120" height="40" rx="6" class="extbox"/>
  <text x="1180" y="316" class="extt">{u(0x7edf,0x4e00,0x8eab,0x4efd,0x8ba4,0x8bc1)}</text>
  <rect x="1120" y="360" width="120" height="48" rx="6" class="extbox"/>
  <text x="1180" y="388" class="extt">{u(0x7269,0x8054,0x7f51,0x5173)}</text>
  <text x="1180" y="402" class="small">{u(0x53ef,0x9009)}</text>
''' + r'''
  <path d="M 655 520 L 1120 172" class="lineaux"/>
  <path d="M 655 750 L 1120 172" class="lineaux"/>
  <path d="M 655 410 L 1120 242" class="lineaux"/>
  <path d="M 140 132 L 1120 310" class="lineaux"/>
  <path d="M 140 202 L 1120 310" class="lineaux"/>
  <path d="M 148 274 L 1120 310" class="lineaux"/>
  <path d="M 148 354 L 1120 310" class="lineaux"/>
  <path d="M 148 444 L 1120 310" class="lineaux"/>
  <path d="M 655 520 L 1120 384" class="lineaux"/>
''' + f'''
  <text x="860" y="330" class="rel">{u(0x00ab,0x4f9d,0x8d56,0x00bb)}</text>
  <text x="860" y="344" class="relData">{u(0x9884,0x7ea6,0x65f6,0x6bb5)}</text>
  <text x="820" y="450" class="rel">{u(0x00ab,0x4f9d,0x8d56,0x00bb)}</text>
  <text x="820" y="464" class="relData">{u(0x53ef,0x7ea6,0x6807,0x5fd7)}</text>
  <text x="780" y="300" class="rel">{u(0x00ab,0x4f9d,0x8d56,0x00bb)}</text>
  <text x="780" y="314" class="relData">{u(0x6458,0x8981,0x67e5,0x8be2)}</text>
  <text x="560" y="260" class="rel">{u(0x00ab,0x4f9d,0x8d56,0x00bb)}</text>
  <text x="560" y="274" class="relData">{u(0x767b,0x5f55)}</text>
  <text x="860" y="440" class="rel">{u(0x00ab,0x4f9d,0x8d56,0x00bb)}</text>
  <text x="860" y="454" class="relData">{u(0x56de,0x8bfb,0x6307,0x4ee4)}</text>

  <text x="660" y="878" text-anchor="middle" class="note">{u(0x56fe,0x4f8b,0xff1a,0x300c,0x5173,0x8054,0x300d,0x53c2,0x4e0e,0x8005,0x2014,0x7528,0x4f8b,0xff1b,0x7070,0x865a,0x7ebf,0x300c,0x4f9d,0x8d56,0x300d,0x3001,0x7d2b,0x300c,0x5305,0x542b,0x300d,0x3001,0x6a59,0x300c,0x6269,0x5c55,0x300d,0x3002)}</text>
</svg>
'''

    out_ops = Path(__file__).with_name("usecase-subsystem-2-operations.svg")
    out_cn = Path(__file__).with_name(
        "usecase-\u5b50\u7cfb\u7edf\u4e8c-\u573a\u5730\u8fd0\u7ef4\u4e0e\u516c\u4f17\u76d1\u7763.svg"
    )
    for p in (out_ops, out_cn):
        p.write_text(svg, encoding="utf-8", newline="\n")
        print("Wrote", p)


if __name__ == "__main__":
    main()
