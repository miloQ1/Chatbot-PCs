import { useEffect, useState } from 'react';
import client from '../api/client';
import type { ProductCategory, ProductResponse } from '../types';

interface UseProductsOptions {
  category?: ProductCategory | null;
  highlightedIds?: number[];
}

interface UseProductsResult {
  products: ProductResponse[];
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

export function useProducts({ category }: UseProductsOptions = {}): UseProductsResult {
  const [products, setProducts] = useState<ProductResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [tick, setTick] = useState(0);

  useEffect(() => {
    setLoading(true);
    setError(null);

    const params = category ? { category } : {};

    client
      .get<ProductResponse[]>('/api/v1/products', { params })
      .then((res) => setProducts(res.data))
      .catch(() => setError('No se pudo cargar el catálogo.'))
      .finally(() => setLoading(false));
  }, [category, tick]);

  return {
    products,
    loading,
    error,
    refetch: () => setTick((t) => t + 1),
  };
}