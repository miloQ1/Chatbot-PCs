import { IonButton } from '@ionic/react';
import type { ProductResponse } from '../types';

const ICONS: Record<string, string> = {
  CPU: '🔲', GPU: '🖥️', RAM: '📦', MOTHERBOARD: '🔧',
  PSU: '⚡', STORAGE: '💾', CASE: '🗄️', COOLER: '❄️',
};

interface Props { product: ProductResponse; }

export function ProductCard({ product }: Props) {
  const specs = product.specs as Record<string, string>;
  const topSpecs = Object.entries(specs).slice(0, 2);
  const searchQuery = encodeURIComponent(product.brand + ' ' + product.name);
  const shoppingUrl = 'https://www.google.com/search?tbm=shop&q=' + searchQuery;

  return (
    <div style={{ background: '#fff', border: '1px solid #e2e8f0', borderRadius: '12px', overflow: 'hidden', width: '160px', flexShrink: 0, boxShadow: '0 1px 4px rgba(0,0,0,0.06)' }}>
      <div style={{ width: '100%', height: '90px', background: '#f8fafc', display: 'flex', alignItems: 'center', justifyContent: 'center', overflow: 'hidden' }}>
        {product.imageUrl ? (
          <img src={product.imageUrl} alt={product.name} style={{ width: '100%', height: '100%', objectFit: 'contain' }} onError={(e) => { (e.target as HTMLImageElement).style.display = 'none'; }} />
        ) : (
          <span style={{ fontSize: '32px' }}>{ICONS[product.category] ?? '🔩'}</span>
        )}
      </div>
      <div style={{ padding: '8px 10px' }}>
        <div style={{ fontSize: '10px', color: '#6b7280', fontWeight: 600, textTransform: 'uppercase' }}>
          {product.category} · {product.brand}
        </div>
        <div style={{ fontWeight: 700, fontSize: '12px', color: '#111827', margin: '2px 0', lineHeight: 1.3 }}>
          {product.name}
        </div>
        <div style={{ fontSize: '10px', color: '#6b7280', marginBottom: '6px' }}>
          {topSpecs.map(([k, v]) => (
            <div key={k}>{k}: <strong>{String(v)}</strong></div>
          ))}
        </div>
        <div style={{ fontWeight: 700, color: '#3b82f6', fontSize: '13px', marginBottom: '6px' }}>
          
        </div>
        <IonButton expand='block' size='small' href={shoppingUrl} target='_blank' rel='noopener noreferrer'>
          Ver en Shopping
        </IonButton>
      </div>
    </div>
  );
}
