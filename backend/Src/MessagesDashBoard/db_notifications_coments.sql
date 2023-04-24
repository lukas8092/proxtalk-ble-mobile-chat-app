CREATE OR REPLACE FUNCTION add_comment_notify()
RETURNS trigger AS
$BODY$

BEGIN
PERFORM pg_notify('new_comment', NEW.ID::text);
RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION add_comment_notify()
OWNER TO postgres;

CREATE OR REPLACE TRIGGER add_comment_event_trigger
AFTER INSERT
ON public.message_comment
FOR EACH ROW
EXECUTE PROCEDURE add_comment_notify();


DROP TRIGGER add_comment_event_trigger on public.message_comment;
DROP FUNCTION add_comment_notify;
