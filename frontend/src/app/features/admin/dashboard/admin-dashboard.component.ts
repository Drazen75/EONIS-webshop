import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent {
  cards = [
    {
      title: 'Proizvodi',
      desc: 'Dodaj, izmeni ili deaktiviraj proizvode u katalogu',
      icon: 'inventory_2',
      route: '/admin/products',
      accent: '#6390c4',
      iconBg: 'rgba(99,144,196,0.08)',
      iconBorder: 'rgba(99,144,196,0.18)',
    },
    {
      title: 'Kategorije',
      desc: 'Upravljaj kategorijama i organizacijom kataloga',
      icon: 'category',
      route: '/admin/categories',
      accent: '#c5a26b',
      iconBg: 'rgba(197,162,107,0.08)',
      iconBorder: 'rgba(197,162,107,0.18)',
    },
    {
      title: 'Porudžbine',
      desc: 'Pregledaj i ažuriraj status svih porudžbina',
      icon: 'receipt_long',
      route: '/admin/orders',
      accent: '#52a882',
      iconBg: 'rgba(82,168,130,0.08)',
      iconBorder: 'rgba(82,168,130,0.18)',
    },
    {
      title: 'Korisnici',
      desc: 'Upravljaj korisničkim nalozima i pristupima',
      icon: 'people',
      route: '/admin/users',
      accent: '#8b7ec4',
      iconBg: 'rgba(139,126,196,0.08)',
      iconBorder: 'rgba(139,126,196,0.18)',
    },
    {
      title: 'Transakcije',
      desc: 'Stripe plaćanja i finansijski pregled',
      icon: 'payments',
      route: '/admin/transactions',
      accent: '#c9952a',
      iconBg: 'rgba(201,149,42,0.08)',
      iconBorder: 'rgba(201,149,42,0.18)',
    },
  ];
}
