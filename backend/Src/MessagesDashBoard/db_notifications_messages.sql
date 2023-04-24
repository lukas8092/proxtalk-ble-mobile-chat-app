CREATE OR REPLACE FUNCTION add_message_notify()
RETURNS trigger AS
$BODY$

BEGIN
PERFORM pg_notify('new_message', NEW.ID::text);
RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION add_message_notify()
OWNER TO postgres;

CREATE OR REPLACE TRIGGER add_message_event_trigger
AFTER INSERT
ON public.message
FOR EACH ROW
EXECUTE PROCEDURE add_message_notify();


DROP TRIGGER add_message_event_trigger on public.message;
DROP FUNCTION add_message_notify;
