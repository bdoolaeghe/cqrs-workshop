#/bin/sh
yes | sudo apt install openjdk-11-jdk
yes | sudo apt install docker
yes | sudo apt install docker-compose
yes | sudo apt install make

## add user to group docker
sudo usermod -aG docker ${USER}
echo ""
echo "please reboot to apply group changes (added to docker group)"
echo "to check your groups: $> id -nG"
echo "expected to belong to docker group"

## check user is in docker group. Else logout/login
# su - ${USER}
# id -nG
# exit

exit 0
