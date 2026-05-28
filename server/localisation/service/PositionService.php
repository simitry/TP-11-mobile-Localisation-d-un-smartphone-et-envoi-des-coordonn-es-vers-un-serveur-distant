<?php

include_once __DIR__ . '/../dao/IDao.php';
include_once __DIR__ . '/../classe/Position.php';
include_once __DIR__ . '/../connexion/Connexion.php';

class PositionService implements IDao {
    private $connexion;

    public function __construct() {
        $this->connexion = new Connexion();
    }

    public function create($position) {
        $sql = "INSERT INTO position(latitude, longitude, date_position, imei)
                VALUES(:latitude, :longitude, :date_position, :imei)";

        $stmt = $this->connexion->getConnexion()->prepare($sql);
        $stmt->execute([
            ':latitude' => $position->getLatitude(),
            ':longitude' => $position->getLongitude(),
            ':date_position' => $position->getDatePosition(),
            ':imei' => $position->getImei()
        ]);

        return $this->connexion->getConnexion()->lastInsertId();
    }

    public function update($obj) {}
    public function delete($obj) {}
    public function getById($obj) {}
    public function getAll() {}
}
