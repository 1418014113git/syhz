@echo off
set gport=8088
for /f "tokens=1-5" %%i in ('netstat -ano^|findstr ":%gport%"') do taskkill /f /pid %%m