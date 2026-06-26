import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { TrackOrderComponent } from './track-order/track-order.component';
import { NewsComponent } from './news/news.component';
import { CompanyComponent } from './company/company.component';
import { ContactComponent } from './contact/contact.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { UserOrdersComponent } from './dashboard/user-orders/user-orders.component';
import { UserOrderDetailComponent } from './dashboard/user-orders/user-order-detail/user-order-detail.component';
import { authGuard } from './auth/auth.guard';
import { roleGuard } from './auth/role.guard';
import { TestComponent } from './dashboard/test/test.component';
import { AddressComponent } from './dashboard/address/address.component';
import { AccountSettingsComponent } from './dashboard/account-settings/account-settings.component';
import { ChangePasswordComponent } from './dashboard/account-settings/change-password/change-password.component';
import { EditInfoComponent } from './dashboard/account-settings/edit-info/edit-info.component';
import { MakeOrderComponent } from './make-order/make-order.component';
import { DeliveryOptionsComponent } from './make-order/delivery-options/delivery-options.component';
import { SenderAddressComponent } from './make-order/sender-address/sender-address.component';
import { RecipientAddressComponent } from './make-order/recipient-address/recipient-address.component';
import { OrderReviewComponent } from './make-order/order-review/order-review.component';
import { PaymentSuccessComponent } from './payment-success/payment-success.component';
import { CourierDashboardComponent } from './courier-dashboard/courier-dashboard.component';
import { DeliveryListComponent } from './courier-dashboard/delivery-list/delivery-list.component';
import { DeliveryHistoryComponent } from './courier-dashboard/delivery-history/delivery-history.component';
import { NotificationsComponent } from './shared/notifications/notifications.component';
import { TicketComponent } from './dashboard/ticket/ticket.component';
import { CreateTicketComponent } from './dashboard/ticket/create-ticket/create-ticket.component';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent, canActivate: [authGuard] },
    { path: 'register', component: RegisterComponent },
    {
      path: 'make-order',
      component: MakeOrderComponent,
      children: [
        { path: 'delivery-options', component: DeliveryOptionsComponent },
        { path: 'sender-address', component: SenderAddressComponent },
        { path: 'recipient-address', component: RecipientAddressComponent },
        { path: 'order-review', component: OrderReviewComponent },
      ]
    },
    { path: 'track-order', component: TrackOrderComponent },
    { path: 'news', component: NewsComponent },
    { path: 'company', component: CompanyComponent },
    { path: 'contact', component: ContactComponent },
    { path: 'shared/notifications', component: NotificationsComponent, canActivate: [authGuard] },
    {
      path: 'dashboard',
       component: DashboardComponent,
       canActivate: [authGuard],
       children: [
        { path: 'user-orders', component: UserOrdersComponent },
        { path: 'user-orders/:id', component: UserOrderDetailComponent },
        { path: 'user-orders/:id/success', component: PaymentSuccessComponent },
        { path: 'test', component: TestComponent },
        { path: 'address', component: AddressComponent },
        {
          path: 'account-settings',
          component: AccountSettingsComponent,
          children: [
            { path: 'change-password', component: ChangePasswordComponent },
            { path: 'edit-info', component: EditInfoComponent },
          ]
        },
        {
          path: 'ticket',
          component: TicketComponent,
          children: [
            { path: 'create-ticket', component: CreateTicketComponent },
            {
              path: 'my-tickets',
              loadComponent: () => import('./tickets/my-tickets/my-tickets.component').then(m =>
                m.MyTicketsComponent)
            },
            { path: 'my-tickets/:id',
              loadComponent: () => import('./tickets/my-tickets/ticket-details/ticket-details.component')
                .then(m => m.TicketDetailsComponent)
            },
          ]
        },
       ],
    },
    {
      path: 'courier-dashboard',
      component: CourierDashboardComponent,
      canActivate: [roleGuard],
      data: {role: 'COURIER'},
      children: [
        { path: 'delivery-list', component: DeliveryListComponent, },
        { path: 'delivery-history', component: DeliveryHistoryComponent },
      ]
    },
    {
      path: 'admin-dashboard',
      loadChildren: () => import('./admin-dashboard/admin-dashboard.routes')
        .then(m => m.adminDashboardRoutes),
    },
    { path: '**', redirectTo: '' },
  ];
