TITLE "Installing Assistant service"

if not "%1"=="am_admin" (powershell start -verb runas '%0' am_admin & exit /b)

SET name=AssistantControlCenter
SET cdir=%~dp0
SET nssm="%cdir%tools\nssm\x64\nssm.exe"
SET java="%JAVA_HOME%\bin\java.exe"

CALL %nssm% stop %name%
CALL %nssm% remove %name% confirm

CALL %nssm% install %name% %java%
CALL %nssm%  set %name% AppDirectory "%cdir%build\libs\."
CALL %nssm%  set %name% AppParameters "-jar -Dserver.port=8081 %cdir%build\libs\assistant-0.9.jar"
CALL %nssm% set %name% ObjectName LocalSystem
CALL %nssm% set %name% Type SERVICE_INTERACTIVE_PROCESS
CALL %nssm% set %name% Start SERVICE_AUTO_START
CALL %nssm% set %name% AppStdout "%cdir%build\libs\service.log"
CALL %nssm% set %name% AppStderr "%cdir%build\libs\service_error.log"

CALL %nssm% start %name%
CALL %nssm% status %name%


pause
