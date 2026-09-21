
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;

public class PedidoRepositoryEnMemoria implements PedidoRepository {

    private final Map<String, Pedido> pedidos = new HashMap<>();

    @Override
    public Optional<Pedido> obtenerPorId(String id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public void guardar(Pedido pedido) {
        pedidos.put(pedidos.getId(), compra);
    }
}

