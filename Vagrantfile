#################################################
# Vagrantfile for StockInference Lab
# @author William Markito <wmarkito@pivotal.io>
#################################################

Vagrant.configure(2) do |config|
  config.vm.box = "package.box"

  config.ssh.forward_agent = true
  config.ssh.insert_key = false
  config.vm.box_check_update = false

  config.vm.network :private_network, ip: "192.168.56.10"
  config.vm.synced_folder ".", "/vagrant_data"

  config.ssh.private_key_path = "~/.vagrant.d/insecure_private_key"

  config.vm.provision "shell", inline: <<-SHELL
    echo "QCon Rio 2015 - Pivotal Lab"
  SHELL

  config.vm.provider :virtualbox do |vb|
    vb.customize ["modifyvm", :id, "--ioapic", "on"]
    vb.customize ["modifyvm", :id, "--cpus", "4"]
    vb.customize ["modifyvm", :id, "--memory", "8192"]
  end
end
