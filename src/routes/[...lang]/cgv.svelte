<script lang="ts">
	import Link from "$shared/Link.svelte";
	import AccordionItem from "$components/accordion/AccordionItem.svelte";

	const siteName: string = "theuntitledcloud.com";
	const ownerName: string = "[Nom du propriétaire du site]";
	const address: string = "[Adresse non disponible]";
	const phoneNumber: string = "[Numéro de téléphone]";
</script>

<svelte:head>
	<title>Conditions générales de vente</title>
</svelte:head>

<h1 id="conditions-generales-de-vente">Conditions générales de vente</h1>

<div class="alert alert-info">
	Le service est encore en version de développement.
	Le document ci-dessous est à l'état d'ébauche et sera mis à jour régulièrement.
</div>

<p><em>En vigueur au 27/08/2022</em></p>
<h2 id="article-1---champ-dapplication">ARTICLE 1 - Champ d’application</h2>
<p> Les présentes Conditions Générales de Vente (dites « CGV ») s’appliquent, sans restriction ni réserve à l’ensemble
	des ventes conclues par le Vendeur auprès d’acheteurs non professionnels (« Les Clients ou le Client »), désirant
	acquérir les produits proposés à la vente (« Les Produits ») par le Vendeur sur le site {siteName}﻿.</p>
<p>Les caractéristiques principales des Produits et notamment les spécifications, illustrations ou de capacité des
	Produits, sont présentées sur le site {siteName} ce dont le client est tenu de prendre connaissance avant de
	commander.</p>
<p>Le choix et l’achat d’un Produit sont de la seule responsabilité du Client.</p>
<p>Les offres de Produits s’entendent dans la limite des stocks disponibles, tels que précisés lors de la passation de
	la commande.</p>
<p>Ces CGV sont accessibles à tout moment sur le site {siteName} et prévaudront sur toute autre document.</p>
<p>Le Client déclare avoir pris connaissance des présentes CGV et les avoir acceptées en cochant la case prévue à cet
	effet avant la mise en œuvre de la procédure de commande en ligne du site {siteName}﻿.</p>
<p>Sauf preuve contraire, les données enregistrées dans le système informatique du Vendeur constituent la preuve de
	l’ensemble des transactions conclues avec le Client.</p>
<p>Les coordonnées du Vendeur sont les suivantes :</p>
<p>{ownerName}</p>
<p>{address}</p>
<p>Numéro d’immatriculation : 123</p>
<p>Email : <a href="mailto:contact@{siteName}">contact@{siteName}</a></p>
<p>Téléphone : {phoneNumber}</p>
<p>En cas de commande vers un pays autre que la France métropolitaine, le Client est l’importateur du ou des Produits
	concernés.</p>
<p>Pour tous les Produits expédiés hors Union européenne et DOM-TOM, le prix sera calculé hors taxes automatiquement sur
	la facture.</p>
<p>Des droits de douane ou autres taxes locales ou droits d’importation ou taxes d’état sont susceptibles d’être
	exigibles. Ils seront à la charge et relèvent de la seule responsabilité du Client.</p>
<h2 id="article-2---prix">ARTICLE 2 - Prix</h2>
<p>Les Produits sont fournis aux tarifs en vigueur figurant sur le site {siteName}﻿, lors de l’enregistrement de la
	commande par le Vendeur.</p>
<p>Les prix sont exprimés en Euros, TTC.</p>
<p>Les tarifs tiennent compte d’éventuelles réductions qui seraient consenties par le Vendeur sur le site
	{siteName}﻿.</p>
<p>Ces tarifs sont fermes et non révisables pendant leur période de validité mais le Vendeur se réserve le droit, hors
	période de validité, d’en modifier les prix à tout moment.</p>
<p>Le paiement demandé au Client correspond au montant total de l’achat.</p>
<p>Une facture est établie par le Vendeur et remise au Client lors du provisionnement des Produits commandés.</p>
<h2 id="article-3-commandes">ARTICLE 3 – Commandes</h2>
<p>Il appartient au Client de sélectionner sur le site {siteName} les Produits qu’il désire commander, selon les
	modalités suivantes :</p>
<p>Le Client choisi un Produit qu’il met dans son panier, Produit qu’il pourra supprimer ou modifier avant de valider sa
	commande et d’accepter les présentes conditions générales de vente. Il rentrera ensuite ses coordonnées ou se
	connectera à son espace. Après validation des informations, la commande sera considérée comme définitive et exigera
	paiement de la part du Client selon les modalités prévues.</p>
<p>Les offres de Produits sont valables tant qu’elles sont visibles sur le site, dans la limite des stocks
	disponibles.</p>
<p>La vente ne sera considérée comme valide qu’après paiement intégral du prix. Il appartient au Client de vérifier
	l’exactitude de la commande et de signaler immédiatement toute erreur.</p>
<p>Toute commande passée sur le site <em>{siteName}</em> constitue la formation d’un contrat conclu à distance entre le
	Client et le Vendeur.</p>
<p>Le Vendeur se réserve le droit d’annuler ou de refuser toute commande d’un Client avec lequel il existerait un litige
	relatif au paiement d’une commande antérieure.</p>
<p>Le Client pourra suivre l’évolution de sa commande sur l’espace client du site.</p>
<h2 id="article-3-bis---espace-client---compte">ARTICLE 3 Bis - Espace client - Compte</h2>
<p>Afin de passer commande, le Client est invité à créer un compte (espace personnel).</p>
<p>Pour ce faire, il doit s’inscrire en remplissant le formulaire qui lui sera proposé au moment de sa commande et
	s’engage à fournir des informations sincères et exactes concernant son état civil et ses coordonnées, notamment son
	adresse email.</p>
<p>Le Client est responsable de la mise à jour des informations fournies. Il lui est précisé qu’il peut les modifier en
	se connectant à son compte.</p>
<p>Pour accéder à son espace personnel et aux historiques de commande, le Client devra s’identifier à l’aide de son nom
	d’utilisateur et de son mot de passe qu’il aura choisi au moment de l’inscription sur le site. A ce titre, le Client
	s’en interdit toute divulgation. Dans le cas contraire, il restera seul responsable de l’usage qui en sera fait.</p>
<p>Le Client pourra également solliciter sa désinscription en se rendant à la page dédiée sur son espace personnel ou
	envoyant un email à : <a href="mailto:contact@{siteName}">contact@{siteName}</a>. Celle-ci sera effective dans un délai raisonnable.</p>
<p>En cas de non respect des conditions générales de vente et/ou d’utilisation, le site {siteName} aura la possibilité
	de suspendre voire de fermer le compte d’un client après mise en demeure adressée par voie électronique et restée
	sans effet.</p>
<p>Toute suppression de compte, quel qu’en soit le motif, engendre la suppression pure et simple de toutes informations
	personnelles du Client.</p>
<p>Tout événement dû à un cas de force majeure ayant pour conséquence un dysfonctionnement du site ou serveur et sous
	réserve de toute interruption ou modification en cas de maintenance, n’engage pas la responsabilité du Vendeur.</p>
<p>La création du compte entraine l’acceptation des présentes conditions générales de vente.</p>
<h2 id="article-4---conditions-de-paiement">ARTICLE 4 - Conditions de paiement</h2>
<p>Le prix est payé par voie de paiement sécurisé, selon les modalités suivantes : - paiement par carte bancaire - ou
	paiement par virement bancaire sur le compte bancaire du Vendeur (dont les coordonnées sont communiquées au Client
	lors de la passation de la commande)</p>
<p>Le prix est payable comptant par le Client, en totalité au jour de la pasation de la commande.</p>
<p>Les données de paiement sont chiffrées et échangées grâce au protocole défini par le prestataire de paiement agréé
	intervenant pour les transactions bancaires réalisée sur le site {siteName}.</p>
<p>Les paiements effectués par le Client ne seront considérés comme définitifs qu’après encaissement effectif par le
	Vendeur des sommes dues.</p>
<p>Le Vendeur ne sera pas tenu de procéder à la délivrance des Produits commandés par le Client si celui-ci ne lui en
	paye pas le prix en totalité dans les conditions ci-dessus indiquées.</p>
<h2 id="article-5---livraisons">ARTICLE 5 - Livraisons</h2>
<p>Les Produits commandés par le Client seront provisionnés du Client dans un délai de moins d’une heure après le
	paiement et seront disponibles sur son espace client.</p>
<p>Le provisionnement est constitué par le transfert au Client du contrôle du Produit. Sauf cas particulier ou
	indisponibilité d’un ou plusieurs Produits, les Produits commandés seront livrés en une seule fois.</p>
<p>Le Vendeur s’engage à faire ses meilleurs efforts pour livrer les produits commandés par le Client dans les délais
	ci-dessus précisés.</p>
<p>Si les Produits commandés n’ont pas été provisionnés dans un délai de 24 heures après la notification de paiement,
	pour toute autre cause que la force majeure ou le fait du Client, la vente pourra être résolue à la demande écrite
	du Client dans les conditions prévues aux articles L 216-2, L 216-3 et L241-4 du Code de la consommation. Les sommes
	versées par le Client lui seront alors restituées au plus tard dans les quatorze jours qui suivent la date de
	dénonciation du contrat, à l’exclusion de toute indemnisation ou retenue.</p>
<p>Le Client est tenu de vérifier l’état des produits livrés. Il dispose d’un délai de 14 jours à compter du
	provisionnement pour formuler des réclamations par <em>par email</em>, accompagnées de tous les justificatifs y
	afférents (photos notamment). Passé ce délai et à défaut d’avoir respecté ces formalités, les Produits seront
	réputés conformes et exempts de tout vice apparent et aucune réclamation ne pourra être valablement acceptée par le
	Vendeur.</p>
<p>Le Vendeur remboursera ou remplacera dans les plus brefs délais et à ses frais, les Produits livrés dont les défauts
	de conformité ou les vices apparents ou cachés auront été dûment prouvés par le Client, dans les conditions prévues
	aux articles L 217-4 et suivants du Code de la consommation et celles prévues aux présentes CGV.</p>
<h2 id="article-6---transfert-de-propriété">ARTICLE 6 - Transfert de propriété</h2>
<p>Le transfert de propriété des Produits du Vendeur au Client ne sera réalisé qu’après complet paiement du prix par ce
	dernier, et ce quelle que soit la date de livraison desdits Produits.</p>
<h2 id="article-7---droit-de-rétractation">ARTICLE 7 - Droit de rétractation</h2>
<p>Conformément à l’Article L221-28 du Code de la consommation et compte tenu de la nature des Produits vendus, les
	commandes passées par le Client ne bénéficient pas du droit de rétractation.</p>
<p>Le contrat est donc conclu de façon définitive dès la passation de la commande par le Client selon les modalités
	précisées aux présentes CGV.</p>
<h2 id="article-8---responsabilité-du-vendeur---garanties">ARTICLE 8 - Responsabilité du Vendeur - Garanties</h2>
<p>Les Produits fournis par le Vendeur bénéficient :</p>
<ul>
	<li>de la garantie légale de conformité, pour les Produits défectueux, abîmés ou endommagés ou ne correspondant pas
		à la commande ;
	</li>
	<li>de la garantie légale contre les vices cachés provenant d’un défaut de matière, de conception ou de fabrication
		affectant les produits livrés et les rendant impropres à l’utilisation ;
	</li>
</ul>
<p><span style="text-decoration:underline;"><em>Dispositions relatives aux garanties légales</em></span></p>



<div class="accordion mb-3">
	<AccordionItem name="Article L217-4 du Code de la consommation">
		<p>Le bien est conforme au contrat s'il répond notamment, le cas échéant, aux critères suivants :<br><br>
		1° Il correspond à la description, au type, à la quantité et à la qualité, notamment en ce qui concerne la fonctionnalité, la compatibilité, l'interopérabilité, ou toute autre caractéristique prévues au contrat ;<br><br>
		2° Il est propre à tout usage spécial recherché par le consommateur, porté à la connaissance du vendeur au plus tard au moment de la conclusion du contrat et que ce dernier a accepté ;<br><br>
		3° Il est délivré avec tous les accessoires et les instructions d'installation, devant être fournis conformément au contrat ;<br><br>
		4° Il est mis à jour conformément au contrat.</p>

		<Link href="https://www.legifrance.gouv.fr/codes/article_lc/LEGIARTI000044142575">Source</Link>
	</AccordionItem>
	<AccordionItem name="Article L217-5 du Code de la consommation">
		<p>I.-En plus des critères de conformité au contrat, le bien est conforme s'il répond aux critères suivants : <br><br>1° Il est propre à l'usage habituellement attendu d'un bien de même type, compte tenu, s'il y a lieu, de toute disposition du droit de l'Union européenne et du droit national ainsi que de toutes les normes techniques ou, en l'absence de telles normes techniques, des codes de conduite spécifiques applicables au secteur concerné ; <br><br>2° Le cas échéant, il possède les qualités que le vendeur a présentées au consommateur sous forme d'échantillon ou de modèle, avant la conclusion du contrat ; <br><br>3° Le cas échéant, les éléments numériques qu'il comporte sont fournis selon la version la plus récente qui est disponible au moment de la conclusion du contrat, sauf si les parties en conviennent autrement ; <br><br>4° Le cas échéant, il est délivré avec tous les accessoires, y compris l'emballage, et les instructions d'installation que le consommateur peut légitimement attendre ; <br><br>5° Le cas échéant, il est fourni avec les mises à jour que le consommateur peut légitimement attendre, conformément aux dispositions de l'article <a href="/affichCodeArticle.do?cidTexte=LEGITEXT000006069565&amp;idArticle=LEGIARTI000032221307&amp;dateTexte=&amp;categorieLien=cid" title="Code de la consommation - art. L217-19 (V)">L. 217-19</a> ; <br><br>6° Il correspond à la quantité, à la qualité et aux autres caractéristiques, y compris en termes de durabilité, de fonctionnalité, de compatibilité et de sécurité, que le consommateur peut légitimement attendre pour des biens de même type, eu égard à la nature du bien ainsi qu'aux déclarations publiques faites par le vendeur, par toute personne en amont dans la chaîne de transactions, ou par une personne agissant pour leur compte, y compris dans la publicité ou sur l'étiquetage. <br><br>II.-Toutefois, le vendeur n'est pas tenu par toutes déclarations publiques mentionnées à l'alinéa qui précède s'il démontre : <br><br>1° Qu'il ne les connaissait pas et n'était légitimement pas en mesure de les connaître ; <br><br>2° Qu'au moment de la conclusion du contrat, les déclarations publiques avaient été rectifiées dans des conditions comparables aux déclarations initiales ; ou <br><br>3° Que les déclarations publiques n'ont pas pu avoir d'influence sur la décision d'achat. <br><br>III.-Le consommateur ne peut contester la conformité en invoquant un défaut concernant une ou plusieurs caractéristiques particulières du bien, dont il a été spécifiquement informé qu'elles s'écartaient des critères de conformité énoncés au présent article, écart auquel il a expressément et séparément consenti lors de la conclusion du contrat.</p>

		<Link href="https://www.legifrance.gouv.fr/codes/article_lc/LEGIARTI000032226939">Source</Link>
	</AccordionItem>
	<AccordionItem name="Article L217-12 du Code de la consommation">
		Le bien est conforme au contrat s'il répond notamment, le cas échéant, aux critères suivants :<br><br>
		1° Il correspond à la description, au type, à la quantité et à la qualité, notamment en ce qui concerne la fonctionnalité, la compatibilité, l'interopérabilité, ou toute autre caractéristique prévues au contrat ;<br><br>
		2° Il est propre à tout usage spécial recherché par le consommateur, porté à la connaissance du vendeur au plus tard au moment de la conclusion du contrat et que ce dernier a accepté ;<br><br>
		3° Il est délivré avec tous les accessoires et les instructions d'installation, devant être fournis conformément au contrat ;<br><br>
		4° Il est mis à jour conformément au contrat.

		<Link href="https://www.legifrance.gouv.fr/codes/article_lc/LEGIARTI000044152610">Source</Link>
	</AccordionItem>
	<AccordionItem name="Article 1641 du Code civil">
		<p>Le vendeur est tenu de la garantie à raison des défauts cachés de la chose vendue qui la rendent impropre à l'usage auquel on la destine, ou qui diminuent tellement cet usage que l'acheteur ne l'aurait pas acquise, ou n'en aurait donné qu'un moindre prix, s'il les avait connus.</p>

		<Link href="https://www.legifrance.gouv.fr/codes/article_lc/LEGIARTI000006441924">Source</Link>
	</AccordionItem>
	<AccordionItem name="Article 1641 du Code civil">
		<p>   Le vendeur est tenu de la garantie à raison des défauts cachés de la chose vendue qui la rendent impropre à l'usage auquel on la destine, ou qui diminuent tellement cet usage que l'acheteur ne l'aurait pas acquise, ou n'en aurait donné qu'un moindre prix, s'il les avait connus.</p>

		<Link href="https://www.legifrance.gouv.fr/codes/article_lc/LEGIARTI000006441924">Source</Link>
	</AccordionItem>
	<AccordionItem name="Article 1648 alinéa 1er du Code civil">
		<p>L'action résultant des vices rédhibitoires doit être intentée par l'acquéreur dans un délai de deux ans à compter de la découverte du vice. </p>

		<Link href="https://www.legifrance.gouv.fr/codes/article_lc/LEGIARTI000020466328">Source</Link>
	</AccordionItem>
	<AccordionItem name="Article L217-16 du Code de la consommation">
		<p>Dans les cas prévus à l'article <a href="/affichCodeArticle.do?cidTexte=LEGITEXT000006069565&amp;idArticle=LEGIARTI000032221293&amp;dateTexte=&amp;categorieLien=cid" title="Code de la consommation - art. L217-14 (V)">L. 217-14</a>, le consommateur informe le vendeur de sa décision de résoudre le contrat. Il restitue les biens au vendeur aux frais de ce dernier. Le vendeur rembourse au consommateur le prix payé et restitue tout autre avantage reçu au titre du contrat. <br><br>Si le défaut de conformité ne porte que sur certains biens délivrés en vertu du contrat de vente, le consommateur a le droit à la résolution du contrat pour l'ensemble des biens, même ceux non couverts par le présent chapitre, si l'on ne peut raisonnablement attendre de lui qu'il accepte de garder les seuls biens conformes. <br><br>Pour les contrats mentionnés au II de l'article L. 217-1, prévoyant la vente de biens et, à titre accessoire, la fourniture de services non couverts par le présent chapitre, le consommateur a droit à la résolution de l'ensemble du contrat. En outre, dans le cas d'une offre groupée au sens de l'article <a href="/affichCodeArticle.do?cidTexte=LEGITEXT000006069565&amp;idArticle=LEGIARTI000033206891&amp;dateTexte=&amp;categorieLien=cid" title="Code de la consommation - art. L224-42-2 (V)">L. 224-42-2</a>, le consommateur a le droit à la résolution de l'ensemble des contrats y afférents. <br><br>Les obligations respectives des parties au contrat, mentionnées à l'article <a href="/affichCodeArticle.do?cidTexte=LEGITEXT000006069565&amp;idArticle=LEGIARTI000044132891&amp;dateTexte=&amp;categorieLien=cid" title="Code de la consommation - art. L224-25-22 (V)">L. 224-25-22</a> et relatives aux conséquences de la résolution pour les contenus numériques et les services numériques, sont applicables à la résolution du contrat de vente d'un bien comportant des éléments numériques.</p>

		<Link href="https://www.legifrance.gouv.fr/codes/article_lc/LEGIARTI000044142535">Source</Link>
	</AccordionItem>
</div>
<h2 id="article-9---données-personnelles">ARTICLE 9 - Données personnelles</h2>
<p>Le Client est informé que la collecte de ses données à caractère personnel est nécessaire à la vente des Produits et
	à leur délivrance / livraison, confiées au Vendeur. Ces données à caractère personnel sont récoltées uniquement pour
	l’exécution du contrat de vente.</p>
<h6 class="fw-bold">9.1 Collecte des données à caractère personnel</h6>
<p>Les données à caractère personnel qui sont collectées sur le site {siteName} sont les suivantes :</p>
<h6 class="fw-bold">Ouverture de compte</h6>
<p>Lors de la création du compte Client / utilisateur : Noms, prénoms, adresse postale et adresse e-mail.</p>
<h6 class="fw-bold">Paiement</h6>
<p>Dans le cadre du paiement des Produits proposés sur le site {siteName}, celui-ci enregistre des données financières
	relatives au compte bancaire ou à la carte de crédit du Client / utilisateur.</p>
<h6 class="fw-bold">9.2 Destinataires des données à caractère personnel</h6>
<p>Les données à caractère personnel sont utilisées par le Vendeur et ses co-contractants pour l’exécution du contrat et
	pour assurer l’efficacité de la prestation de vente et de délivrance des Produits.</p>
<p>La ou les catégorie(s) de co-contractant(s) est (sont) :
</p>
	<ul>
	<li>Les prestataires établissements de paiement (<Link href="https://stripe.com/">Stripe</Link>) ;</li>
</ul>
<h6 class="fw-bold">9.3 Responsable de traitement</h6>
<p>Le responsable de traitement des données est le Vendeur, au sens de la loi Informatique et libertés et à compter du
	25 mai 2018 du Règlement 2016/679 sur la protection des données à caractère personnel.</p>
<h6 class="fw-bold">9.4 Limitation du traitement</h6>
<p>Sauf si le Client exprime son accord exprès, ses données à caractère personnelles ne sont pas utilisées à des fins
	publicitaires ou marketing.</p>
<h6 class="fw-bold">9.5 Durée de conservation des données</h6>
<p>Le Vendeur conservera les données ainsi recueillies pendant un délai de 5 ans, couvrant le temps de la prescription
	de la responsabilité civile contractuelle applicable.</p>
<h6 class="fw-bold">9.6 Sécurité et confidentialité</h6>
<p>Le Vendeur met en œuvre des mesures organisationnelles, techniques, logicielles et physiques en matière de sécurité
	du numérique pour protéger les données personnelles contre les altérations, destructions et accès non autorisés.
	Toutefois il est à signaler qu’Internet n’est pas un environnement complètement sécurisé et le Vendeur ne peut
	garantir la sécurité de la transmission ou du stockage des informations sur Internet.</p>
<h6 class="fw-bold">9.7 Mise en œuvre des droits des Clients et utilisateurs</h6>
<p>En application de la règlementation applicable aux données à caractère personnel, les Clients et utilisateurs du site
	{siteName} disposent des droits suivants :</p>
<ul>
	<li>Ils peuvent mettre à jour ou supprimer les données qui les concernent en se connectant à son compte ;</li>
	<li>Ils peuvent supprimer leur compte ;</li>
	<li>Ils peuvent exercer leur droit d’accès pour connaître les données personnelles les concernant via l'espace client ;</li>
	<li>Si les données à caractère personnel détenues par le Vendeur sont inexactes, ils peuvent demander la mise à jour des informations des informations ;</li>
	<li>Ils peuvent demander la suppression de leurs données à caractère personnel, conformément aux lois applicables en matière de protection des données ;</li>
	<li>Ils peuvent également solliciter la portabilité des données détenues par le Vendeur vers un autre prestataire ;</li>
	<li>Enfin, ils peuvent s’opposer au traitement de leurs données par le Vendeur ;</li>
</ul>
<p>
	Ces droits, dès lors qu’ils ne s’opposent pas à la finalité du traitement, peuvent être exercé via l'espace client ou en adressant une
	demande par E-mail au Responsable de traitement dont les coordonnées sont indiquées ci-dessus.
</p>
<p>Le responsable de traitement doit apporter une réponse dans un délai maximum d’un mois.</p>
<p>En cas de refus de faire droit à la demande du Client, celui-ci doit être motivé.</p>
<p>Le Client est informé qu’en cas de refus, il peut introduire une réclamation auprès de la CNIL (3 place de Fontenoy,
	75007 PARIS) ou saisir une autorité judiciaire.</p>
<p>Le Client peut être invité à cocher une case au titre de laquelle il accepte de recevoir des mails à caractère
	informatifs et publicitaires de la part du Vendeur. Il aura toujours la possibilité de retirer son accord à tout
	moment en contactant le Vendeur (coordonnées ci-dessus) ou en suivant le lien de désabonnement.</p>

<h2 id="article-10---propriété-intellectuelle">ARTICLE 10 - Propriété intellectuelle</h2>
<p>Le contenu du site {siteName} est la propriété du Vendeur et de ses partenaires et est protégé par les lois
	françaises et internationales relatives à la propriété intellectuelle.</p>
<p>Toute reproduction totale ou partielle de ce contenu est strictement interdite et est susceptible de constituer un
	délit de contrefaçon.</p>
<h2 id="article-11---droit-applicable---langue">ARTICLE 11 - Droit applicable - Langue</h2>
<p>Les présentes CGV et les opérations qui en découlent sont régies et soumises au droit français.</p>
<p>Les présentes CGV sont rédigées en langue française. Dans le cas où elles seraient traduites en une ou plusieurs
	langues étrangères, seul le texte français ferait foi en cas de litige.</p>
<h2 id="article-12---litiges">ARTICLE 12 - Litiges</h2>
<p>Pour toute réclamation merci de contacter le service clientèle à l’adresse postale ou mail du Vendeur indiquée à
	l’ARTICLE 1 des présentes CGV.</p>
<p>Le Client est informé qu’il peut en tout état de cause recourir à une médiation conventionnelle, auprès des instances
	de médiation sectorielles existantes ou à tout mode alternatif de règlement des différends (conciliation, par
	exemple) en cas de contestation. </p>
<p>En l’espèce, le médiateur désigné est</p>
<p>[Nom du service de médiation]</p>
<p>[Adresse postale du service de médiation]</p>
<p>[Site internet du service de médiation]</p>
<p>E-mail : [Adresse e-mail du service de médiation]. </p>
<p>Le Client est également informé qu’il peut, également recourir à la plateforme de Règlement en Ligne des Litige (RLL)
	: https://webgate.ec.europa.eu/odr/main/index.cfm?event=main.home.show</p>
<p>Tous les litiges auxquels les opérations d’achat et de vente conclues en application des présentes CGV et qui
	n’auraient pas fait l’objet d’un règlement amiable entre le vendeur ou par médiation, seront soumis aux tribunaux
	compétents dans les conditions de droit commun.</p>