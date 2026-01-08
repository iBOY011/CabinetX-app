-- Migration: Add MISSING status to rendez_vous check constraint
-- Date: 2026-01-08
-- Description: Adds MISSING as a valid status for appointments (absent patients)

-- Connect to appointment database
\c appointment_db;

-- Drop the existing check constraint
ALTER TABLE rendez_vous DROP CONSTRAINT IF EXISTS rendez_vous_statutrdv_check;

-- Add the new check constraint with MISSING included
ALTER TABLE rendez_vous ADD CONSTRAINT rendez_vous_statutrdv_check 
CHECK (statutrdv IN ('CONFIRME', 'EN_ATTENTE', 'EN_CONSULTATION', 'ANNULE', 'TERMINE', 'MISSING'));

-- Verify the constraint was added
SELECT conname, pg_get_constraintdef(oid) 
FROM pg_constraint 
WHERE conname = 'rendez_vous_statutrdv_check';

COMMIT;
