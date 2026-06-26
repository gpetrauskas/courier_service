import { Routes } from '@angular/router';
import { roleGuard } from '../auth/role.guard';
import { AdminDashboardComponent } from './admin-dashboard.component';
import { ManageUserComponent } from './manage-user/manage-user.component';
import { ManageOrderComponent } from './manage-order/manage-order.component';
import { ManageDeliveryOptionComponent } from './manage-delivery-option/manage-delivery-option.component';
import { AllUsersComponent } from './manage-user/all-users/all-users.component';
import { UserDetailsComponent } from './manage-user/user-details/user-details.component';
import { AllOrdersComponent } from './manage-order/all-orders/all-orders.component';
import { OrderDetailsComponent } from './manage-order/order-details/order-details.component';
import { AllDeliveryOptionsComponent } from './manage-delivery-option/all-delivery-options/all-delivery-options.component';
import { AddDeliveryOptionComponent } from './manage-delivery-option/add-delivery-option/add-delivery-option.component';
import { DeliveryOptionDetailsComponent } from './manage-delivery-option/delivery-option-details/delivery-option-details.component';
import { FindUserComponent } from './manage-user/find-user/find-user.component';
import { DeliveryControlComponent } from './delivery-control/delivery-control.component';
import { AllListsComponent } from './delivery-control/all-lists/all-lists.component';
import { CreateNewListComponent } from './delivery-control/create-new-list/create-new-list.component';
import { ListDetailsComponent } from './delivery-control/list-details/list-details.component';
import { ManageCourierComponent } from './manage-courier/manage-courier.component';
import { RegisterCourierComponent } from './manage-courier/register-courier/register-courier.component';
import { NotificationComponent } from './notification/notification.component';
import { CreateComponent } from './notification/create/create.component';
import { ManageComponent } from './notification/manage/manage.component';

export const adminDashboardRoutes: Routes = [
  {
    path: '',
    component: AdminDashboardComponent,
    canActivate: [roleGuard],
    data: { role: 'ADMIN' },
    children: [
      {
        path: 'manage-user',
        component: ManageUserComponent,
        children: [
          { path: 'all-users', component: AllUsersComponent },
          { path: 'find-user', component: FindUserComponent },
          { path: 'user-details/:id', component: UserDetailsComponent },
        ]
      },
      {
        path: 'manage-order',
        component: ManageOrderComponent,
        children: [
          { path: 'all-orders', component: AllOrdersComponent },
          { path: 'order-details/:id', component: OrderDetailsComponent }
        ]
      },
      {
        path: 'manage-delivery-option',
        component: ManageDeliveryOptionComponent,
        children: [
          { path: 'add-delivery-option', component: AddDeliveryOptionComponent },
          { path: 'all-delivery-options', component: AllDeliveryOptionsComponent },
          { path: 'delivery-option-details/:id', component: DeliveryOptionDetailsComponent }
        ]
      },
      {
        path: 'delivery-control',
        component: DeliveryControlComponent,
        children: [
          { path: 'all-lists', component: AllListsComponent },
          { path: 'create-new-list', component: CreateNewListComponent },
          { path: 'list-details/:id', component: ListDetailsComponent }
        ]
      },
      {
        path: 'manage-courier',
        component: ManageCourierComponent,
        children: [
          { path: 'register-courier', component: RegisterCourierComponent },
        ]
      },
      {
        path: 'notification',
        component: NotificationComponent,
        children: [
          { path: 'create', component: CreateComponent },
          { path: 'manage', component: ManageComponent }
        ]
      },
      {
        path: 'tickets/all',
        loadComponent: () => import('../tickets/my-tickets/my-tickets.component').then(m =>
          m.MyTicketsComponent)
      },
      {
        path: 'tickets/all/:id',
        loadComponent: () => import('../tickets/my-tickets/ticket-details/ticket-details.component').then(m =>
          m.TicketDetailsComponent)
      },
    ]
  },
];
