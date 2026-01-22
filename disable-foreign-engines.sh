#!/bin/sh
# 禁用国外搜索引擎，只保留国内可用的引擎

SETTINGS_FILE="/etc/searxng/settings.yml"

# 禁用 Google 相关
sed -i '/- name: google$/a\    disabled: true' $SETTINGS_FILE
sed -i '/- name: google images$/a\    disabled: true' $SETTINGS_FILE
sed -i '/- name: google news$/a\    disabled: true' $SETTINGS_FILE
sed -i '/- name: google videos$/a\    disabled: true' $SETTINGS_FILE
sed -i '/- name: google scholar$/a\    disabled: true' $SETTINGS_FILE

# 禁用 DuckDuckGo
sed -i '/- name: duckduckgo$/a\    disabled: true' $SETTINGS_FILE

# 禁用 Wikipedia
sed -i '/- name: wikipedia$/a\    disabled: true' $SETTINGS_FILE

# 禁用 Brave
sed -i '/- name: brave$/a\    disabled: true' $SETTINGS_FILE

# 禁用 Startpage
sed -i '/- name: startpage$/a\    disabled: true' $SETTINGS_FILE

echo "已禁用国外搜索引擎，重启 SearXNG 以生效"
