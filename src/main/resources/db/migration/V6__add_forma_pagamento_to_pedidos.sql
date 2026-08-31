ALTER TABLE pedidos ADD COLUMN forma_pagamento VARCHAR(20);

UPDATE pedidos SET forma_pagamento = 'BOLETO_A_VISTA' WHERE forma_pagamento IS NULL;

ALTER TABLE pedidos ALTER COLUMN forma_pagamento SET NOT NULL;
