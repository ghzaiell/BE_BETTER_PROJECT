import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllProgrammesComponent } from './all-programmes.component';

describe('AllProgrammesComponent', () => {
  let component: AllProgrammesComponent;
  let fixture: ComponentFixture<AllProgrammesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllProgrammesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllProgrammesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
