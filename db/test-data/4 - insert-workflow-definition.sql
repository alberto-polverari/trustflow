-- INSERT test data workflow definition

INSERT INTO public.workflow_definition (id, "name", tenant_id, "type") VALUES(1, 'Test Srl', 1, 'SEQUENZIALE');
INSERT INTO public.workflow_definition (id, "name", tenant_id, "type") VALUES(2, 'Tenant2 Srl', 2, 'PARALLELO');