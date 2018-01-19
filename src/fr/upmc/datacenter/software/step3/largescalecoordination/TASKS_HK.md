Coordiantions à grande échelle ( gestion des AVM et gestion de consommation d'énergie et réseau logique des contrôleurs autonomiques)

Pour assurer une coordination directe entre les contrôleurs autonomiques, il est nécessaire de les
mettre en réseau pair-à-pair. Il s’agit donc de construire un réseau logique incluant tous les contrô-
leurs autonomiques, permettant donc l’échange entre toutes paires de contrôleurs quelconques. De
nombreuses topologies et de nombreux algorithmes pour construire de tels réseaux ont été pro-
posés et étudiés, comme nous le voyons dans le cours magistral, mais pour les besoins du projet,
nous allons nous contenter de quelque chose de très simple. Les contrôleurs autonomiques vont
simplement être joints par des liens pour former un anneau qui sera géré par le contrôleur d’admis-
sion pour les insertions et par les contrôleurs autonomiques eux-mêmes pour les retraits. Les liens
du réseau doivent servir à passer les informations de coordination, qui sont décrites dans les deux
prochaines sous-sections. Il s’agit donc simplement de connexions normales entre composants, mais
prévues pour échanger les données voulues.
La gestion décentralisée des machines virtuelles allouées puis désallouées aux applications peut
se faire par la circulation d’identifiants de machines virtuelles libres sur le réseau logique des
contrôleurs autonomiques. Un contrôleur souhaitant ajouter une machine virtuelle à son application doit 
attendre d’obtenir un identifiant de machine virtuelle libre pour l’utiliser. Lorsqu’un
contrôleur désalloue une machine virtuelle, il en force la réinitialisation puis émet son identifiant
sur le réseau logique.

Dans un premier temps, il suffira de supposer que toutes les machines virtuelles sont créées une
fois pour toute et leurs identifiants circulent sur le réseau logique tant qu’elles sont libres. Dans un
second temps, une solution mixte peut être employée pour diminuer et augmenter le nombre de
machines virtuelles disponibles sur le centre en insérant le contrôleur d’admission dans le réseau
logique pour lui permettre de voir passer les identifiants de machines virtuelles libres et ainsi
décider, en fonction de sa fréquence de réception d’identifiants et de sa gestion de consommation
d’énergie, de retirer ou ajouter de nouvelles machines virtuelles.