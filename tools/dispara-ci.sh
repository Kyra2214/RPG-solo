#!/usr/bin/env bash
# Dispara o Android CI pelo GitHub CLI (`gh`) na branch atual.
# Uso: tools/dispara-ci.sh [branch] [e2e=true|false] [python=true|false]
set -euo pipefail
branch="${1:-$(git rev-parse --abbrev-ref HEAD)}"
e2e="${2:-true}"
py="${3:-true}"
git push -u origin "$branch"
gh workflow run android-ci.yml --ref "$branch" -f e2e="$e2e" -f python="$py"
sleep 5
gh run list --workflow android-ci.yml --branch "$branch" --limit 1
echo "Acompanhe com: gh run watch"
