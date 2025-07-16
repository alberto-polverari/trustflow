-- INSERT test data workflow_step

INSERT INTO public.workflow_step (id, workflow_definition_id, approver_id, step_order) VALUES(1, 1, 'admin', 1);
INSERT INTO public.workflow_step (id, workflow_definition_id, approver_id, step_order) VALUES(2, 2, 'approver1', 1);
INSERT INTO public.workflow_step (id, workflow_definition_id, approver_id, step_order) VALUES(3, 2, 'approver2', 2);
INSERT INTO public.workflow_step (id, workflow_definition_id, approver_id, step_order) VALUES(4, 2, 'approverTenant2', 3);