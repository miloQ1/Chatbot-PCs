import { useState, useMemo } from 'react';
import { useProducts } from '../../hooks/useProducts';
import type { ProductCategory } from '../../types';
import { CategoryFilter } from './CategoryFilter';
import { ProductCard } from './ProductCard';
import { Spinner } from '../ui/Spinner';

interface Props {
  highlightedIds: number[];
}

export function CatalogPanel({ highlightedIds }: Props) {
  const [category, setCategory] = useState<ProductCategory | null>(null);
  const [search, setSearch] = useState('');
  const [brand, setBrand] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');

  const { products, loading, error } = useProducts({ category });

  const brands = useMemo(() => {
    const set = new Set(products.map((p) => p.brand));
    return Array.from(set).sort();
  }, [products]);

  const filtered = useMemo(() => {
    return products.filter((p) => {
      const matchSearch = search === '' ||
        p.name.toLowerCase().includes(search.toLowerCase()) ||
        p.brand.toLowerCase().includes(search.toLowerCase());
      const matchBrand = brand === '' || p.brand === brand;
      const matchMin = minPrice === '' || p.priceClp >= Number(minPrice);
      const matchMax = maxPrice === '' || p.priceClp <= Number(maxPrice);
      return matchSearch && matchBrand && matchMin && matchMax;
    });
  }, [products, search, brand, minPrice, maxPrice]);

  const inputStyle = {
    padding: '8px 12px',
    borderRadius: '8px',
    border: '1px solid #e2e8f0',
    fontSize: '13px',
    outline: 'none',
    width: '100%',
    background: '#fff',
    color: '#111827',
  };

  return (
    <div style={{ padding: '20px', display: 'flex', flexDirection: 'column', gap: '0' }}>

      {/* Header del catalogo */}
      <div style={{
        background: 'linear-gradient(135deg, #1e40af 0%, #3b82f6 100%)',
        borderRadius: '12px',
        padding: '20px',
        marginBottom: '16px',
        color: '#fff',
      }}>
        <h2 style={{ margin: '0 0 4px', fontSize: '20px', fontWeight: 800 }}>
          Catálogo de Hardware
        </h2>
        <p style={{ margin: 0, fontSize: '13px', opacity: 0.85 }}>
          {products.length} componentes disponibles · Usa el asesor para encontrar tu configuración ideal
        </p>
      </div>

      {/* Filtro categorias */}
      <CategoryFilter selected={category} onChange={setCategory} />

      {/* Filtros */}
      <div style={{
        background: '#fff',
        border: '1px solid #e2e8f0',
        borderRadius: '12px',
        padding: '16px',
        margin: '12px 0',
        display: 'flex',
        flexDirection: 'column',
        gap: '12px',
      }}>
        <input
          type="text"
          placeholder="Buscar por nombre o marca..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={inputStyle}
        />
        <div style={{ display: 'flex', gap: '8px' }}>
          <select
            value={brand}
            onChange={(e) => setBrand(e.target.value)}
            style={{ ...inputStyle, flex: 1 }}
          >
            <option value="">Todas las marcas</option>
            {brands.map((b) => (
              <option key={b} value={b}>{b}</option>
            ))}
          </select>
        </div>
        <div style={{ display: 'flex', gap: '8px' }}>
          <input
            type="number"
            placeholder="Precio mínimo"
            value={minPrice}
            onChange={(e) => setMinPrice(e.target.value)}
            style={{ ...inputStyle, flex: 1 }}
          />
          <input
            type="number"
            placeholder="Precio máximo"
            value={maxPrice}
            onChange={(e) => setMaxPrice(e.target.value)}
            style={{ ...inputStyle, flex: 1 }}
          />
        </div>
        {(search || brand || minPrice || maxPrice) && (
          <button
            onClick={() => { setSearch(''); setBrand(''); setMinPrice(''); setMaxPrice(''); }}
            style={{
              padding: '7px',
              borderRadius: '8px',
              border: '1px solid #e2e8f0',
              background: '#f8fafc',
              fontSize: '12px',
              color: '#6b7280',
              cursor: 'pointer',
              fontWeight: 500,
            }}
          >
            Limpiar filtros
          </button>
        )}
      </div>

      {/* Contador */}
      {!loading && !error && (
        <div style={{ fontSize: '12px', color: '#94a3b8', marginBottom: '12px', fontWeight: 500 }}>
          {filtered.length} resultado{filtered.length !== 1 ? 's' : ''}
          {filtered.length !== products.length && ` de ${products.length}`}
        </div>
      )}

      {loading && (
        <div style={{ display: 'flex', justifyContent: 'center', marginTop: '40px' }}>
          <Spinner />
        </div>
      )}

      {error && (
        <div style={{ color: '#ef4444', fontSize: '14px' }}>{error}</div>
      )}

      {!loading && !error && filtered.length === 0 && (
        <div style={{ textAlign: 'center', color: '#9ca3af', marginTop: '40px', fontSize: '14px' }}>
          No se encontraron productos con esos filtros.
        </div>
      )}

      {!loading && !error && (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(160px, 1fr))',
          gap: '12px',
        }}>
          {filtered.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              highlighted={highlightedIds.includes(product.id)}
            />
          ))}
        </div>
      )}
    </div>
  );
}