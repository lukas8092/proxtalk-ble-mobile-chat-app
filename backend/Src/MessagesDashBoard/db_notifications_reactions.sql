CREATE OR REPLACE FUNCTION update_reactions_notify()
RETURNS trigger AS
$BODY$

BEGIN
PERFORM pg_notify('update_reactions', NEW.message_id::text);
RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION update_reactions_notify()
OWNER TO postgres;

CREATE OR REPLACE FUNCTION remove_reactions_notify()
RETURNS trigger AS
$BODY$

BEGIN
PERFORM pg_notify('update_reactions', OLD.message_id::text);
RETURN NEW;
END;
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;
ALTER FUNCTION update_reactions_notify()
OWNER TO postgres;

CREATE OR REPLACE TRIGGER update_reactions_event_trigger
AFTER INSERT
ON public.message_reaction
FOR EACH ROW
EXECUTE PROCEDURE update_reactions_notify();

CREATE OR REPLACE TRIGGER remove_reactions_event_trigger
AFTER DELETE
ON public.message_reaction
FOR EACH ROW
EXECUTE PROCEDURE remove_reactions_notify();


DROP TRIGGER update_reactions_event_trigger on public.message_reaction;
DROP FUNCTION update_reactions_notify;
