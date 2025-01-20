#
# This file is part of Visual Code Space.
#
# Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
# the GNU General Public License as published by the Free Software Foundation, either version 3 of
# the License, or (at your option) any later version.
#
# Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along with Visual Code Space.
# If not, see <https://www.gnu.org/licenses/>.
#

#https://github.com/Xed-Editor/Xed-Editor/blob/main/core/main/src/main/assets/terminal/init.sh
set -e  # Exit immediately on Failure

export PATH=/bin:/sbin:/usr/bin:/usr/sbin:/usr/share/bin:/usr/share/sbin:/usr/local/bin:/usr/local/sbin
export HOME=/home
export PROMPT_DIRTRIM=2
export PS1="\[\e[38;5;46m\]\u\[\033[39m\]@vcspace \[\033[39m\]\w \[\033[0m\]\\$ "
START_SHELL="/bin/bash"
required_packages="bash nano sudo file build-base"
missing_packages=""
for pkg in $required_packages; do
    if ! apk info -e $pkg >/dev/null 2>&1; then
        missing_packages="$missing_packages $pkg"
    fi
done
if [ -n "$missing_packages" ]; then
    echo -e "\e[34;1m[*] \e[37mInstalling Important packages\e[0m"
    apk update && apk upgrade
    apk add $missing_packages
    if [ $? -eq 0 ]; then
        echo -e "\e[32;1m[+] \e[37mSuccessfully Installed\e[0m"
    fi
    echo -e "\e[34m[*] \e[37mUse \e[32mapk\e[37m to install new packages\e[0m"
fi

#fix linker warning
if [[ ! -f /linkerconfig/ld.config.txt ]];then
    mkdir -p /linkerconfig
    touch /linkerconfig/ld.config.txt
fi

if [ "$#" -eq 0 ]; then
    $START_SHELL
else
    # shellcheck disable=SC2068
    $@
fi
