INSERT INTO public.liq_db_changelog
(id, author, filename, dateexecuted, orderexecuted, exectype, md5sum, description, "comments", tag, liquibase, contexts, labels, deployment_id)
SELECT src.id, src.author, src.filename, src.dateexecuted, src.orderexecuted, src.exectype, src.md5sum, src.description, src."comments", src.tag, src.liquibase, src.contexts, src.labels, src.deployment_id
FROM SOURCE_TABLE AS src
ON CONFLICT ('/* insert on conflict attributes here, e.g. ID, ... */')
/* or you may use [DO NOTHING;] */
DO UPDATE
SET id=EXCLUDED.id, author=EXCLUDED.author, filename=EXCLUDED.filename, dateexecuted=EXCLUDED.dateexecuted, orderexecuted=EXCLUDED.orderexecuted, exectype=EXCLUDED.exectype, md5sum=EXCLUDED.md5sum, description=EXCLUDED.description, "comments"=EXCLUDED."comments", tag=EXCLUDED.tag, liquibase=EXCLUDED.liquibase, contexts=EXCLUDED.contexts, labels=EXCLUDED.labels, deployment_id=EXCLUDED.deployment_id;

INSERT INTO public.liq_db_changelog_lock
(id, "locked", lockgranted, lockedby)
SELECT src.id, src."locked", src.lockgranted, src.lockedby
FROM SOURCE_TABLE AS src
ON CONFLICT (id)
/* or you may use [DO NOTHING;] */
DO UPDATE
SET "locked"=EXCLUDED."locked", lockgranted=EXCLUDED.lockgranted, lockedby=EXCLUDED.lockedby;
