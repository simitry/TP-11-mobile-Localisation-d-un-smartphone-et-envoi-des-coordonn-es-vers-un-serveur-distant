# Backend PHP SQLite

Ce dossier remplace MySQL/phpMyAdmin par SQLite.

Lancer le serveur PHP depuis la racine du projet :

```powershell
php -S 0.0.0.0:8000 -t server/localisation
```

Depuis l'émulateur Android, l'URL utilisée par l'application est :

```text
http://10.0.2.2:8000/createPosition.php
```

La base SQLite est créée automatiquement ici :

```text
server/localisation/data/localisation.sqlite
```
