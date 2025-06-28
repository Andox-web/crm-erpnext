-- Création de la table
CREATE TABLE salary_condition_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    base DOUBLE NOT NULL,
    sign INT NOT NULL,
    conditions JSON NOT NULL COMMENT 'JSON array des conditions appliquées',
    affected_salary_slips JSON NOT NULL COMMENT 'JSON array des bulletins modifiés',
    status ENUM('SUCCESS', 'FAILED') NOT NULL,
    error_message TEXT
);

-- 1. Insertion de données d'exemple
INSERT INTO salary_condition_history (base, sign, conditions, affected_salary_slips, status)
VALUES (
    10.0,
    1,
    '[{"component": "Basic Salary", "operator": ">", "value": 1000.0}, {"component": "Transport", "operator": "<=", "value": 200.0}]',
    '["SLIP-2023-001", "SLIP-2023-002"]',
    'SUCCESS'
);

INSERT INTO salary_condition_history (base, sign, conditions, affected_salary_slips, status, error_message)
VALUES (
    5.0,
    -1,
    '[{"component": "Bonus", "operator": ">=", "value": 500.0}]',
    '[]',
    'FAILED',
    'Erreur de calcul: division par zéro'
);

-- 2. Sélection de toutes les données
SELECT * FROM salary_condition_history;

-- 3. Requêtes avec fonctions JSON
-- Sélectionner la première condition de chaque historique
SELECT 
    id,
    JSON_EXTRACT(conditions, '$[0].component') AS first_component,
    JSON_EXTRACT(conditions, '$[0].operator') AS first_operator,
    JSON_EXTRACT(conditions, '$[0].value') AS first_value
FROM salary_condition_history;

-- Trouver les historiques qui contiennent une condition sur "Basic Salary"
SELECT *
FROM salary_condition_history
WHERE JSON_SEARCH(conditions, 'one', 'Basic Salary') IS NOT NULL;

-- Compter le nombre de bulletins affectés par opération
SELECT
    id,
    JSON_LENGTH(affected_salary_slips) AS slips_count
FROM salary_condition_history;

-- 4. Mise à jour d'un enregistrement
-- Ajouter un message d'erreur à un enregistrement
UPDATE salary_condition_history
SET error_message = 'Erreur de connexion à l''API'
WHERE id = 2;

-- Ajouter une nouvelle condition à un historique existant
UPDATE salary_condition_history
SET conditions = JSON_ARRAY_APPEND(
    conditions,
    '$',
    JSON_OBJECT(
        'component', 'Medical Allowance',
        'operator', '=',
        'value', 150.0
    )
)
WHERE id = 1;

-- 5. Modification de la structure de la table
-- Ajouter une colonne 'user'
ALTER TABLE salary_condition_history
ADD COLUMN user VARCHAR(50) NULL COMMENT 'Utilisateur ayant lancé l''opération';

-- Modifier le type d'une colonne
ALTER TABLE salary_condition_history
MODIFY COLUMN base DECIMAL(10,2) NOT NULL;

-- Renommer une colonne
ALTER TABLE salary_condition_history
RENAME COLUMN sign TO operation_sign;

-- 6. Suppression de données
-- Supprimer les historiques échoués
DELETE FROM salary_condition_history
WHERE status = 'FAILED';

-- 7. Suppression de la table
-- DROP TABLE salary_condition_history;

-- 8. Requêtes complexes
-- Trouver les opérations avec plus de 2 conditions
SELECT *
FROM salary_condition_history
WHERE JSON_LENGTH(conditions) > 2;

-- Statistiques par statut
SELECT
    status,
    COUNT(*) AS operation_count,
    AVG(base) AS average_base,
    SUM(JSON_LENGTH(affected_salary_slips)) AS total_affected_slips
FROM salary_condition_history
GROUP BY status;