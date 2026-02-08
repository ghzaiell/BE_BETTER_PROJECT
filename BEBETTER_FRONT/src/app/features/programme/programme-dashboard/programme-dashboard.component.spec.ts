import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProgrammeDashboardComponent } from './programme-dashboard.component';

describe('ProgrammeDashboardComponent', () => {
  let component: ProgrammeDashboardComponent;
  let fixture: ComponentFixture<ProgrammeDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProgrammeDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProgrammeDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
