#/bin/sh
yes | sudo apt install openjdk-11-jdk
yes | sudo apt install docker
yes | sudo apt install docker-compose
yes | sudo apt install make

## add user to group docker
sudo usermod -aG docker ${USER}
su - ${USER}

## check user is in docker group. Else logout/login
id -nG
