-- INSERT test data utente

INSERT INTO public.utente (id, username, codice_fiscale, "password", "role", tenant_id) VALUES(2, 'utente1', 'PLVLRT86R24Z112H', '$2a$12$yAm7IL5aVejDjuYxkyPYrO93JVREEK7F2GdugYf5jXqKYrMhP20Ty', 'USER', 1);
INSERT INTO public.utente (id, username, codice_fiscale, "password", "role", tenant_id) VALUES(1, 'approver1', 'PLVLRT86R24Z112H', '$2a$12$yAm7IL5aVejDjuYxkyPYrO93JVREEK7F2GdugYf5jXqKYrMhP20Ty', 'APPROVER', 2);
INSERT INTO public.utente (id, username, codice_fiscale, "password", "role", tenant_id) VALUES(3, 'approver2', 'PLVLRT86R24Z112H', '$2a$12$yAm7IL5aVejDjuYxkyPYrO93JVREEK7F2GdugYf5jXqKYrMhP20Ty', 'APPROVER', 2);
INSERT INTO public.utente (id, username, codice_fiscale, "password", "role", tenant_id) VALUES(4, 'approverTenant2', 'PLVLRT86R24Z112H', '$2a$12$yAm7IL5aVejDjuYxkyPYrO93JVREEK7F2GdugYf5jXqKYrMhP20Ty', 'APPROVER', 2);