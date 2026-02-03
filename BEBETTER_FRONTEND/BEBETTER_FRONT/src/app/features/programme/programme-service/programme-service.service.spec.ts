import { TestBed } from '@angular/core/testing';

import { ProgrammeServiceService } from './programme-service.service';

describe('ProgrammeServiceService', () => {
  let service: ProgrammeServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProgrammeServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
