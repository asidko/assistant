if not "%1"=="am_admin" (powershell start -verb runas '%0' am_admin & exit /b)

SET name=AssistantControlCenter

SET cdir=%~dp0
SET nssm="%cdir%tools\nssm\x64\nssm.exe"

CALL %nssm% stop %name%
CALL %nssm% remove %name% confirm
