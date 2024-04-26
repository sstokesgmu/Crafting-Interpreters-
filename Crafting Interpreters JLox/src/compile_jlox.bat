@echo off
for /r %%i in (*.java) do (
    javac "%%i"
)