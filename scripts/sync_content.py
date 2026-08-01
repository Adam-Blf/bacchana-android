#!/usr/bin/env python3
"""Syncs La Taverne content packs from ../la-taverne-content into the Android app.

Free packs (premium=false) are copied verbatim into app/src/main/assets/packs/
so the app works fully offline with zero network calls. Premium packs never
ship their prompt text - only metadata (id, mode, title, subtitle, intensity,
item count) lands in app/src/main/assets/premium-catalog.json, used to render
locked tiles until Play Billing / RevenueCat is wired up.

Usage: python scripts/sync_content.py
Reproducible and idempotent - safe to re-run any time la-taverne-content changes.
"""
from __future__ import annotations

import json
import shutil
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parent.parent
CONTENT_ROOT = REPO_ROOT.parent / "la-taverne-content"
PACKS_SRC = CONTENT_ROOT / "content" / "fr" / "packs"
ASSETS_DIR = REPO_ROOT / "app" / "src" / "main" / "assets"
PACKS_DEST = ASSETS_DIR / "packs"
PREMIUM_CATALOG_DEST = ASSETS_DIR / "premium-catalog.json"


def main() -> int:
    if not PACKS_SRC.is_dir():
        print(f"ERROR: content source not found at {PACKS_SRC}", file=sys.stderr)
        return 1

    PACKS_DEST.mkdir(parents=True, exist_ok=True)
    for stale in PACKS_DEST.glob("*.json"):
        stale.unlink()

    free_count = 0
    premium_entries: list[dict] = []

    for pack_file in sorted(PACKS_SRC.glob("*.json")):
        data = json.loads(pack_file.read_text(encoding="utf-8"))
        meta = data["pack"]

        if meta["premium"]:
            premium_entries.append(
                {
                    "id": meta["id"],
                    "mode": meta["mode"],
                    "title": meta["title"],
                    "subtitle": meta.get("subtitle", ""),
                    "intensity": meta.get("intensity", "medium"),
                    "itemCount": len(data["items"]),
                }
            )
            continue

        shutil.copyfile(pack_file, PACKS_DEST / pack_file.name)
        free_count += 1

    PREMIUM_CATALOG_DEST.write_text(
        json.dumps(premium_entries, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )

    print(f"Synced {free_count} free packs -> {PACKS_DEST}")
    print(f"Wrote {len(premium_entries)} premium catalog entries -> {PREMIUM_CATALOG_DEST}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
