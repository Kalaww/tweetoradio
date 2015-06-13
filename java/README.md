
Les options de ligne de commande sont prioritaires devant un fichier de configuration.
Vous pouvez donc creer un fichier de configuration commun à tous les clients/diffuseurs/gestionnaires
puis personnaliser chacun (id, port ...) avec les options de ligne de commande


=== GESTIONNAIRE ===

# Commande
	./gestionnaire_run.sh [options]

# Options
	-c [fichier] : fichier de configuration
	-p [numero] : port TCP
	-m [numero] : nombre maximum de diffuseur
	-d : active le mode debug
	-outL : sortie des logs
	-outD : sortie des debugs

# Fichier de configuration
	port_tcp : port TCP
	max_diffuseurs : nombre maximum de diffuseur
	debug : active le mode debug
	out_log : sortie des logs
	out_debug : sortie des debugs




== DIFFUSEUR ===

# Commande
	./diffuseur_run.sh [options]

# Options
	-c [fichier] : fichier de configuration
	-id [nom] : id du diffuseur
	-p [numero] : port TCP
	-ipD [addresse] : addresse IPv4 de multi diffusion
	-pD [numero] : port de multi diffusion
	-ipG [addresse] : addresse IPv4 du gestionnaire
	-pG [addresse] : port du gestionnaire
	-d : active le mode debug
	-outL : sortie des logs
	-outD : sortie des debugs

# Fichier de configuration
	id : id du diffuseur
	port_tcp : port TCP
	ip_diffusion : addresse IPv4 de multi diffusion
	port_diffusion : port de multi diffusion
	ip_gestionnaire : addresse IPv4 du gestionnaire
	port_gestionnaire : port du gestionnaire
	debug : active le mode debug
	out_log : sortie des logs
	out_debug : sortie des debugs




=== CLIENT ===

# Commande
	./client_run.sh [options]

# Options
	-c [fichier] : fichier de configuration
	-id [nom] : id du client
	-ipG [addresse] : addresse IPv4 du gestionnaire
	-pG [addresse] : port du gestionnaire
	-d : active le mode debug
	-outL : sortie des logs
	-outD : sortie des debugs
	-out1 : sortie une (menu, interaction avec l'utilisateur)
	-out2 : sortie deux (messages des diffuseurs)

# Fichier de configuration
	id : id du client
	ip_gestionnaire : addresse IPV4 du gestionnaire
	port_gestionnaire : port du gestionnaire
	debug : active le debug (1 ou true)
	out_log : sortie des logs
	out_debug : sortie des debugs
	out_1 : sortie une (menu, interaction avec l'utilisateur)
	out_2 : sortie deux (messages des diffuseurs)

