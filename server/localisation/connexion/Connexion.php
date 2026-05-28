<?php

class Connexion {
    private $connexion;

    public function __construct() {
        $dataDir = __DIR__ . '/../data';
        if (!is_dir($dataDir)) {
            mkdir($dataDir, 0777, true);
        }

        $databasePath = $dataDir . '/localisation.sqlite';
        $this->connexion = new PDO('sqlite:' . $databasePath);
        $this->connexion->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        $schema = file_get_contents(__DIR__ . '/../schema.sql');
        $this->connexion->exec($schema);
    }

    public function getConnexion() {
        return $this->connexion;
    }
}
