-- Add metadata columns to notification table for patient consultation notifications
ALTER TABLE notification 
ADD COLUMN IF NOT EXISTS patient_id BIGINT,
ADD COLUMN IF NOT EXISTS appointment_id BIGINT;

-- Add index for faster queries by appointmentId
CREATE INDEX IF NOT EXISTS idx_notification_appointment_id ON notification(appointment_id);

-- Add index for faster queries by patientId
CREATE INDEX IF NOT EXISTS idx_notification_patient_id ON notification(patient_id);
