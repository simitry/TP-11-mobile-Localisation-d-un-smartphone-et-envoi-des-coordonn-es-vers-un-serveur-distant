<?php

header('Content-Type: text/plain; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo 'Méthode non autorisée';
    exit;
}

include_once __DIR__ . '/service/PositionService.php';

$required = ['latitude', 'longitude', 'date_position', 'imei'];
foreach ($required as $field) {
    if (!isset($_POST[$field]) || $_POST[$field] === '') {
        http_response_code(400);
        echo 'Champ manquant : ' . $field;
        exit;
    }
}

$position = new Position(
    null,
    $_POST['latitude'],
    $_POST['longitude'],
    $_POST['date_position'],
    $_POST['imei']
);

$service = new PositionService();
$id = $service->create($position);

echo 'Position enregistrée avec succès. ID SQLite serveur : ' . $id;
