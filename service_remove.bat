SET name=assistant-control-center

SET cdir=%~dp0
SET nssm="%cdir%tools\nssm\x64\nssm.exe"

CALL %nssm% stop %name%
CALL %nssm% remove %name% confirm
