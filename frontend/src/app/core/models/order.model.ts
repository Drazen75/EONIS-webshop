export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  productImageUrl: string;
  quantity: number;
  priceAtPurchase: number;
  subtotal: number;
}

export interface Order {
  id: number;
  userId: number;
  userEmail: string;
  userFullName: string;
  status: string;
  totalAmount: number;
  shippingAddress: string;
  stripeSessionId: string;
  items: OrderItem[];
  createdAt: string;
}

export interface Transaction {
  id: number;
  orderId: number;
  stripeSessionId: string;
  stripePaymentIntentId: string;
  amount: number;
  currency: string;
  customerEmail: string;
  status: string;
  createdAt: string;
  userFullName: string;
  shippingAddress: string;
  itemCount: number;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
  role: string;
  active: boolean;
  createdAt: string;
}
