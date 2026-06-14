export type ProductCategory =
  | 'CPU' | 'GPU' | 'RAM' | 'MOTHERBOARD'
  | 'PSU' | 'STORAGE' | 'CASE' | 'COOLER';

export interface ProductResponse {
  id: number;
  category: ProductCategory;
  brand: string;
  name: string;
  specs: Record<string, unknown>;
  useCaseTags: string[];
  priceClp: number;
  imageUrl: string | null;
}

export type MessageRole = 'USER' | 'ASSISTANT';

export interface MessageResponse {
  id: number;
  role: MessageRole;
  content: string;
  createdAt: string;
}

export interface ConversationResponse {
  id: number;
  title: string;
  createdAt: string;
  updatedAt: string;
  messages: MessageResponse[];
}

export interface ChatMessageResponse {
  userMessage: MessageResponse;
  assistantMessage: MessageResponse;
  recommendedProducts: ProductResponse[];
}