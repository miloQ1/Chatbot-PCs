import type { ProductCategory } from '../../types';

const CATEGORIES: { value: ProductCategory | null; label: string }[] = [
  { value: null, label: 'Todos' },
  { value: 'CPU', label: 'CPU' },
  { value: 'GPU', label: 'GPU' },
  { value: 'RAM', label: 'RAM' },
  { value: 'MOTHERBOARD', label: 'Placa Madre' },
  { value: 'PSU', label: 'Fuente' },
  { value: 'STORAGE', label: 'Almacenamiento' },
  { value: 'CASE', label: 'Gabinete' },
  { value: 'COOLER', label: 'Refrigeración' },
];

interface Props {
  selected: ProductCategory | null;
  onChange: (category: ProductCategory | null) => void;
}

export function CategoryFilter({ selected, onChange }: Props) {
  return (
    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', padding: '12px 0' }}>
      {CATEGORIES.map(({ value, label }) => (
        <button
          key={label}
          onClick={() => onChange(value)}
          style={{
            padding: '6px 14px',
            borderRadius: '999px',
            border: 'none',
            cursor: 'pointer',
            fontSize: '13px',
            fontWeight: 500,
            background: selected === value ? '#3b82f6' : '#e2e8f0',
            color: selected === value ? '#fff' : '#374151',
            transition: 'all 0.15s',
          }}
        >
          {label}
        </button>
      ))}
    </div>
  );
}