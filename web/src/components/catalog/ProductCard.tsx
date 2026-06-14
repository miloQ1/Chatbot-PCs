import type { ProductResponse } from '../../types';

// Icono de categoria como fallback cuando no hay imageUrl
const CATEGORY_ICONS: Record<string, string> = {
  CPU: '🔲',
  GPU: '🖥️',
  RAM: '📦',
  MOTHERBOARD: '🔧',
  PSU: '⚡',
  STORAGE: '💾',
  CASE: '🗄️',
  COOLER: '❄️',
};

interface Props {
  product: ProductResponse;
  highlighted: boolean;
}

export function ProductCard({ product, highlighted }: Props) {
  const specs = product.specs as Record<string, string>;

  return (
    <div style={{
      border: highlighted ? '2px solid #3b82f6' : '1px solid #e2e8f0',
      borderRadius: '12px',
      padding: '12px',
      background: highlighted ? '#eff6ff' : '#fff',
      transition: 'all 0.2s',
      position: 'relative',
    }}>
      {highlighted && (
        <span style={{
          position: 'absolute',
          top: '8px',
          right: '8px',
          background: '#3b82f6',
          color: '#fff',
          fontSize: '11px',
          fontWeight: 600,
          padding: '2px 8px',
          borderRadius: '999px',
        }}>
          Recomendado
        </span>
      )}

      {/* Imagen o icono de categoria */}
      <div style={{
        width: '100%',
        height: '100px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: '#f8fafc',
        borderRadius: '8px',
        marginBottom: '10px',
        overflow: 'hidden',
      }}>
        {product.imageUrl ? (
          <img
            src={product.imageUrl}
            alt={product.name}
            style={{ width: '100%', height: '100%', objectFit: 'contain' }}
            onError={(e) => {
              // Si la imagen falla, muestra el icono de la categoria
              (e.target as HTMLImageElement).style.display = 'none';
            }}
          />
        ) : (
          <span style={{ fontSize: '40px' }}>
            {CATEGORY_ICONS[product.category] ?? '🔩'}
          </span>
        )}
      </div>

      <div style={{ fontSize: '11px', color: '#6b7280', fontWeight: 600, textTransform: 'uppercase' }}>
        {product.category} · {product.brand}
      </div>
      <div style={{ fontWeight: 600, fontSize: '14px', margin: '4px 0', color: '#111827' }}>
        {product.name}
      </div>

      {/* Specs clave */}
      <div style={{ fontSize: '12px', color: '#6b7280', marginBottom: '8px' }}>
        {Object.entries(specs).slice(0, 2).map(([k, v]) => (
          <span key={k} style={{ marginRight: '8px' }}>
            {k}: <strong>{String(v)}</strong>
          </span>
        ))}
      </div>

      <div style={{ fontWeight: 700, color: '#3b82f6', fontSize: '15px' }}>
        ${product.priceClp.toLocaleString('es-CL')}
      </div>
    </div>
  );
}