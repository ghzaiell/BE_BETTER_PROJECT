import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProgrammeLayoutComponent } from './programme-layout/programme-layout.component';
import { AllProgrammesComponent } from './all-programmes/all-programmes.component';
import { ProgrammeDetailComponent } from './programme-detail/programme-detail.component';
import { ProgrammeDashboardComponent } from './programme-dashboard/programme-dashboard.component';
const routes: Routes = [
  { path: '', redirectTo: 'layout', pathMatch: 'full' },
  {path:'layout',component:ProgrammeLayoutComponent,
    children:[
      { path: '', redirectTo: 'allprogrammes', pathMatch: 'full' },
      { path: 'allprogrammes', component:AllProgrammesComponent},
      { path: 'dashboard', component:ProgrammeDashboardComponent},
      { path: 'programme/:id', component:ProgrammeDetailComponent},
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProgrammeRoutingModule { }
