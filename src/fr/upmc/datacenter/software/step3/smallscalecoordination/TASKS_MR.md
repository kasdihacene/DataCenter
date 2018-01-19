Coordinations à petite échelle ( fréquences des Cores et d’allocation des cœurs aux AVM )

Coordinations à petite échelle
Jusqu’ici, nous avons volontairement considéré que la modification de la fréquence des cœurs
de même que la modification du nombre de cœurs affectés aux machines virtuelles pouvaient se
réaliser de manière autonome au niveau des contrôleurs de chaque application. Malheureusement,
les choses ne sont pas si simples.
Pour la fréquence des cœurs, rappelons qu’il existe une contrainte physique sur les processeurs
qui font que les fréquences de ses cœurs ne peuvent prendre des valeurs arbitraires car les différences
de fréquences sont contraintes. Considérons le cas où deux applications A et B possèdent des
machines virtuelles partageant les cœurs d’un même processeur. Si A souhaitent relever la fréquence
de ses cœurs mais que B au contraire veut les diminuer, il faut arriver à résoudre ce conflit. Pour
cela, on peut créer un arbitre par processeur qui va coordonner les demandes de modifications des
fréquences de ses cœurs. On peut imaginer plusieurs politiques de coordination. Par exemple, on
peut supposer que si une application demande de relever la fréquence d’un ou plusieurs cœurs maisque cela crée une différence trop grande, le coordinateur accepte temporairement une différence
trop importante, mais impose aux autres contrôleurs des autres applications ayant des cœurs sur
le même processur de modifier leurs fréquences dans le même sens dès leur prochaine décision.
Un problème similaire se présente pour l’allocation des cœurs aux machines virtuelles. Rappe-
lons ici aussi qu’il y a une contrainte imposée qui dit que tous les cœurs d’une machine virtuelle
doivent se trouver sur un même ordinateur. Lorsqu’une machine virtuelle souhaite avoir un nou-
veau cœur, elle doit en trouver un libre sur l’ordinateur où elle tourne. Si deux machines virtuelles
sur le même ordinateur veulent toutes les deux un cœur, il faut décider laquelle va l’obtenir. On
peut ici aussi établir une coordinateur à petite échelle en créant un arbitre au niveau de chaque
ordinateur auquel les contrôleurs des applications possédant des machines virtuelles sur cet ordi-
nateur doivent s’adresser pour obtenir (et rendre) les cœurs. Encore une fois, plusieurs politiques
de coordination peuvent être appliquées, mais on peut imaginer une politique simple qui consiste
à demander un cœur au moment de la lecture des capteurs au cas où le contrôleur en aurait besoin
pendant le cycle de contrôle courant, et s’il y en a un disponible, il est réservé à ce contrôleur
jusqu’à ce qu’il ait pris sa décision. S’il décide de l’utiliser, il peut confirmer la chose à l’arbitre,
sinon il lui rend simplement le cœur qui redevient disponible pour une autre application.

Enfin, la même problématique, se retrouve au niveau du centre de calcul pour l’allocation des
machines virtuelles. Si deux applications savent qu’une machine virtuelle est libre, elles peuvent
toutes les deux décider de la prendre, mais alors un conflit peut se produire pour l’obtention de
cette ressource. On peut imaginer une solution à petite échelle, similaire à celle qu’on vient d’évo-
quer pour les cœurs, c’est-à-dire que le contrôleur d’admission maintient un groupe de machines
virtuelles libres, et au début de chaque cycle de contrôle, les applications en réservent une au cas
où elles en auraient besoin, et soi confirment l’utilisation ou la rendent en fin de cycle.
Malheureusement, une telle approche ne passe pas à l’échelle : plus il y aura d’applications, plus
il y aura de demandes de réservation faites au contrôleur d’admission qui finira par être submergé
si le nombre d’applications augmente trop. Une solution décentralisée est préférable dans ce cas,
ce que nous allons maintenant voir.
Pour la coordination à petite échelle, les solutions centralisées sont souvent les plus simples à
mettre en œuvre. Deux contrôles se prêtent à de telles soltuions : le contrôle des fréquences des
cœurs et le contrôle de l’allocation des cœurs aux machines virtuelles. Dans ces deux cas, il est
possible d’associer une entité de coordination à chaque processeur et de faire en sorte que tous les
contrôleurs d’applications ayant des machines virtuelles s’exécutant sur une processeur soient liés
par une interface de coordination à ce coordonnateur. Chaque modification de la fréquence d’un
des cœurs du processeur et chaque modificaiton de l’allocation des cœurs à des machines virtuelles
devra passer par ce coordonnateur pour lui permettre d’exercer les politiques de coordination
choisies.