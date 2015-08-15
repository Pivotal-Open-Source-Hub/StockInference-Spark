Vagrant.configure(2) do |config|
  config.vm.box = "ubuntu/vivid64"

  # Disable automatic box update checking. If you disable this, then
  # boxes will only be checked for updates when the user runs
  # `vagrant box outdated`. This is not recommended.
  config.ssh.forward_agent = true
  config.ssh.insert_key = false
  config.vm.box_check_update = false

  # Create a forwarded port mapping which allows access to a specific port
  # within the machine from a port on the host machine. In the example below,
  # accessing "localhost:8080" will access port 80 on the guest machine.
  # config.vm.network "forwarded_port", guest: 80, host: 8080
  config.vm.network :private_network, ip: "192.168.56.10"

  config.vm.synced_folder ".", "/vagrant_data"

  config.ssh.private_key_path = "~/.vagrant.d/insecure_private_key"

  config.vm.provision "shell", inline: <<-SHELL
    sudo hostname "stocks-vm"
    echo "QCON Rio 2015"
  SHELL

  #
  # config.vm.define :vm do |vm|
  #   vm.config.hostname = "stocks-vm"
  # end


  config.vm.provider :virtualbox do |vb|
    vb.customize ["modifyvm", :id, "--ioapic", "on"]
    vb.customize ["modifyvm", :id, "--cpus", "4"]
    vb.customize ["modifyvm", :id, "--memory", "8192"]
  end
end
