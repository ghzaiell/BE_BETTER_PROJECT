import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProgrammeLayoutComponent } from './programme-layout/programme-layout.component';
import { AllProgrammesComponent } from './all-programmes/all-programmes.component';
const routes: Routes = [
  { path: '', redirectTo: 'layout', pathMatch: 'full' },
  {path:'layout',component:ProgrammeLayoutComponent,
    children:[
      { path: '', redirectTo: 'allprogrammes', pathMatch: 'full' },
      { path: 'allprogrammes', component:AllProgrammesComponent},
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProgrammeRoutingModule { }
