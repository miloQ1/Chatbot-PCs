import type { MessageResponse, ProductResponse } from '../types';
import { ProductCard } from './ProductCard';


interface Props {
  message: MessageResponse;
  recommendedProducts: ProductResponse[];
}

export function MessageBubble({ message, recommendedProducts }: Props) {
  const isUser = message.role === 'USER';

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: isUser ? 'flex-end' : 'flex-start',
      marginBottom: '16px',
      padding: '0 16px',
    }}>
      <div style={{
        maxWidth: '82%',
        padding: '10px 14px',
        borderRadius: isUser ? '18px 18px 4px 18px' : '18px 18px 18px 4px',
        background: isUser ? '#3b82f6' : '#f1f5f9',
        color: isUser ? '#fff' : '#111827',
        fontSize: '14px',
        lineHeight: 1.6,
        whiteSpace: 'pre-wrap',
      }}>
        {message.content}
      </div>

      {!isUser && recommendedProducts.length > 0 && (
        <div style={{
          display: 'flex',
          gap: '10px',
          marginTop: '10px',
          overflowX: 'auto',
          maxWidth: '100%',
          paddingBottom: '4px',
        }}>
          {recommendedProducts.map((p) => (
            <ProductCard key={p.id} product={p} />
          ))}
        </div>
      )}
    </div>
  );
}