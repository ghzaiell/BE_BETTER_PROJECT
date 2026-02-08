import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProgrammeRoutingModule } from './programme-routing.module';
import { RouterModule } from '@angular/router';
import { ProgrammeLayoutComponent } from './programme-layout/programme-layout.component';


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ProgrammeRoutingModule,RouterModule,
    ProgrammeLayoutComponent
  ]
})
export class ProgrammeModule { }
